package com.safety.women.ui

import android.app.Dialog
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.textfield.TextInputEditText
import com.safety.women.R
import com.safety.women.data.EmergencyContact
import com.safety.women.databinding.ActivityContactsBinding
import com.safety.women.databinding.ItemContactBinding
import com.safety.women.util.PermissionsManager
import com.safety.women.viewmodel.ContactsViewModel

/**
 * Activity to manage emergency contacts
 */
class ContactsActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityContactsBinding
    private lateinit var viewModel: ContactsViewModel
    private lateinit var permissionsManager: PermissionsManager
    private lateinit var adapter: ContactsAdapter
    
    private val pickContactLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            result.data?.data?.let { uri ->
                handleContactPicked(uri)
            }
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContactsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        
        viewModel = ViewModelProvider(this)[ContactsViewModel::class.java]
        permissionsManager = PermissionsManager(this)
        
        setupRecyclerView()
        observeContacts()
        
        binding.fabAddContact.setOnClickListener {
            showAddContactDialog()
        }
        
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }
    
    private fun setupRecyclerView() {
        adapter = ContactsAdapter(
            onEdit = { contact -> showEditContactDialog(contact) },
            onDelete = { contact -> showDeleteConfirmationDialog(contact) }
        )
        
        binding.contactsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.contactsRecyclerView.adapter = adapter
    }
    
    private fun observeContacts() {
        viewModel.allContacts.observe(this) { contacts ->
            adapter.submitList(contacts)
            
            if (contacts.isEmpty()) {
                binding.emptyView.visibility = View.VISIBLE
                binding.contactsRecyclerView.visibility = View.GONE
            } else {
                binding.emptyView.visibility = View.GONE
                binding.contactsRecyclerView.visibility = View.VISIBLE
            }
        }
    }
    
    private fun showAddContactDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_contact, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()
        
        val nameEditText = dialogView.findViewById<TextInputEditText>(R.id.nameEditText)
        val phoneEditText = dialogView.findViewById<TextInputEditText>(R.id.phoneEditText)
        val relationshipEditText = dialogView.findViewById<TextInputEditText>(R.id.relationshipEditText)
        val primaryCheckBox = dialogView.findViewById<MaterialCheckBox>(R.id.primaryCheckBox)
        val pickContactButton = dialogView.findViewById<MaterialButton>(R.id.pickContactButton)
        val saveButton = dialogView.findViewById<MaterialButton>(R.id.saveButton)
        val cancelButton = dialogView.findViewById<MaterialButton>(R.id.cancelButton)
        
        pickContactButton.setOnClickListener {
            if (permissionsManager.hasContactsPermissions()) {
                pickContact()
                dialog.dismiss()
            } else {
                permissionsManager.requestContactsPermissions()
            }
        }
        
        saveButton.setOnClickListener {
            val name = nameEditText.text.toString().trim()
            val phone = phoneEditText.text.toString().trim()
            val relationship = relationshipEditText.text.toString().trim()
            val isPrimary = primaryCheckBox.isChecked
            
            if (name.isEmpty()) {
                Toast.makeText(this, R.string.error_empty_name, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            if (phone.isEmpty()) {
                Toast.makeText(this, R.string.error_invalid_phone, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            val contact = EmergencyContact(
                name = name,
                phoneNumber = phone,
                relationship = relationship,
                isPrimary = isPrimary
            )
            
            viewModel.insertContact(contact)
            Toast.makeText(this, R.string.contact_added, Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        
        cancelButton.setOnClickListener {
            dialog.dismiss()
        }
        
        dialog.show()
    }
    
    private fun showEditContactDialog(contact: EmergencyContact) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_contact, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()
        
        val nameEditText = dialogView.findViewById<TextInputEditText>(R.id.nameEditText)
        val phoneEditText = dialogView.findViewById<TextInputEditText>(R.id.phoneEditText)
        val relationshipEditText = dialogView.findViewById<TextInputEditText>(R.id.relationshipEditText)
        val primaryCheckBox = dialogView.findViewById<MaterialCheckBox>(R.id.primaryCheckBox)
        val pickContactButton = dialogView.findViewById<MaterialButton>(R.id.pickContactButton)
        val saveButton = dialogView.findViewById<MaterialButton>(R.id.saveButton)
        val cancelButton = dialogView.findViewById<MaterialButton>(R.id.cancelButton)
        
        // Pre-fill with existing data
        nameEditText.setText(contact.name)
        phoneEditText.setText(contact.phoneNumber)
        relationshipEditText.setText(contact.relationship)
        primaryCheckBox.isChecked = contact.isPrimary
        pickContactButton.visibility = View.GONE
        
        saveButton.setOnClickListener {
            val name = nameEditText.text.toString().trim()
            val phone = phoneEditText.text.toString().trim()
            val relationship = relationshipEditText.text.toString().trim()
            val isPrimary = primaryCheckBox.isChecked
            
            if (name.isEmpty()) {
                Toast.makeText(this, R.string.error_empty_name, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            if (phone.isEmpty()) {
                Toast.makeText(this, R.string.error_invalid_phone, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            val updatedContact = contact.copy(
                name = name,
                phoneNumber = phone,
                relationship = relationship,
                isPrimary = isPrimary
            )
            
            viewModel.updateContact(updatedContact)
            Toast.makeText(this, R.string.contact_updated, Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        
        cancelButton.setOnClickListener {
            dialog.dismiss()
        }
        
        dialog.show()
    }
    
    private fun showDeleteConfirmationDialog(contact: EmergencyContact) {
        AlertDialog.Builder(this)
            .setTitle(R.string.confirm_delete)
            .setMessage(R.string.confirm_delete_message)
            .setPositiveButton(R.string.yes) { _, _ ->
                viewModel.deleteContact(contact)
                Toast.makeText(this, R.string.contact_deleted, Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton(R.string.no, null)
            .show()
    }
    
    private fun pickContact() {
        val intent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
        pickContactLauncher.launch(intent)
    }
    
    private fun handleContactPicked(uri: Uri) {
        val cursor: Cursor? = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
                val idIndex = it.getColumnIndex(ContactsContract.Contacts._ID)
                
                val name = if (nameIndex >= 0) it.getString(nameIndex) else ""
                val contactId = if (idIndex >= 0) it.getString(idIndex) else ""
                
                // Get phone number
                val phoneCursor = contentResolver.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                    arrayOf(contactId),
                    null
                )
                
                phoneCursor?.use { pc ->
                    if (pc.moveToFirst()) {
                        val phoneIndex = pc.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                        val phoneNumber = if (phoneIndex >= 0) pc.getString(phoneIndex) else ""
                        
                        // Show dialog with pre-filled data
                        showAddContactDialogWithData(name, phoneNumber)
                    }
                }
            }
        }
    }
    
    private fun showAddContactDialogWithData(name: String, phone: String) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_contact, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()
        
        val nameEditText = dialogView.findViewById<TextInputEditText>(R.id.nameEditText)
        val phoneEditText = dialogView.findViewById<TextInputEditText>(R.id.phoneEditText)
        val relationshipEditText = dialogView.findViewById<TextInputEditText>(R.id.relationshipEditText)
        val primaryCheckBox = dialogView.findViewById<MaterialCheckBox>(R.id.primaryCheckBox)
        val pickContactButton = dialogView.findViewById<MaterialButton>(R.id.pickContactButton)
        val saveButton = dialogView.findViewById<MaterialButton>(R.id.saveButton)
        val cancelButton = dialogView.findViewById<MaterialButton>(R.id.cancelButton)
        
        nameEditText.setText(name)
        phoneEditText.setText(phone)
        pickContactButton.visibility = View.GONE
        
        saveButton.setOnClickListener {
            val contactName = nameEditText.text.toString().trim()
            val contactPhone = phoneEditText.text.toString().trim()
            val relationship = relationshipEditText.text.toString().trim()
            val isPrimary = primaryCheckBox.isChecked
            
            if (contactName.isEmpty()) {
                Toast.makeText(this, R.string.error_empty_name, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            if (contactPhone.isEmpty()) {
                Toast.makeText(this, R.string.error_invalid_phone, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            val contact = EmergencyContact(
                name = contactName,
                phoneNumber = contactPhone,
                relationship = relationship,
                isPrimary = isPrimary
            )
            
            viewModel.insertContact(contact)
            Toast.makeText(this, R.string.contact_added, Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        
        cancelButton.setOnClickListener {
            dialog.dismiss()
        }
        
        dialog.show()
    }
    
    /**
     * RecyclerView Adapter for emergency contacts
     */
    private class ContactsAdapter(
        private val onEdit: (EmergencyContact) -> Unit,
        private val onDelete: (EmergencyContact) -> Unit
    ) : RecyclerView.Adapter<ContactsAdapter.ContactViewHolder>() {
        
        private var contacts = listOf<EmergencyContact>()
        
        fun submitList(newContacts: List<EmergencyContact>) {
            contacts = newContacts
            notifyDataSetChanged()
        }
        
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
            val binding = ItemContactBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return ContactViewHolder(binding)
        }
        
        override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
            holder.bind(contacts[position], onEdit, onDelete)
        }
        
        override fun getItemCount() = contacts.size
        
        class ContactViewHolder(private val binding: ItemContactBinding) : 
            RecyclerView.ViewHolder(binding.root) {
            
            fun bind(
                contact: EmergencyContact,
                onEdit: (EmergencyContact) -> Unit,
                onDelete: (EmergencyContact) -> Unit
            ) {
                binding.contactName.text = contact.name
                binding.contactPhone.text = contact.phoneNumber
                binding.contactRelationship.text = contact.relationship
                
                binding.primaryBadge.visibility = if (contact.isPrimary) View.VISIBLE else View.GONE
                
                binding.editButton.setOnClickListener { onEdit(contact) }
                binding.deleteButton.setOnClickListener { onDelete(contact) }
            }
        }
    }
}
