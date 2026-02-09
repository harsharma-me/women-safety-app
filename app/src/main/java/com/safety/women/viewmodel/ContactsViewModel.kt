package com.safety.women.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.safety.women.data.AppDatabase
import com.safety.women.data.EmergencyContact
import kotlinx.coroutines.launch

/**
 * ViewModel for managing emergency contacts
 */
class ContactsViewModel(application: Application) : AndroidViewModel(application) {
    
    private val database = AppDatabase.getDatabase(application)
    private val contactDao = database.emergencyContactDao()
    
    // LiveData for all contacts
    val allContacts: LiveData<List<EmergencyContact>> = contactDao.getAllContacts().asLiveData()
    
    /**
     * Insert a new contact
     */
    fun insertContact(contact: EmergencyContact) {
        viewModelScope.launch {
            // If this is marked as primary, clear other primary flags
            if (contact.isPrimary) {
                contactDao.clearAllPrimaryFlags()
            }
            contactDao.insertContact(contact)
        }
    }
    
    /**
     * Update an existing contact
     */
    fun updateContact(contact: EmergencyContact) {
        viewModelScope.launch {
            // If this is marked as primary, clear other primary flags
            if (contact.isPrimary) {
                contactDao.clearAllPrimaryFlags()
            }
            contactDao.updateContact(contact)
        }
    }
    
    /**
     * Delete a contact
     */
    fun deleteContact(contact: EmergencyContact) {
        viewModelScope.launch {
            contactDao.deleteContact(contact)
        }
    }
    
    /**
     * Get contact count
     */
    suspend fun getContactCount(): Int {
        return contactDao.getContactCount()
    }
    
    /**
     * Get primary contact
     */
    suspend fun getPrimaryContact(): EmergencyContact? {
        return contactDao.getPrimaryContact()
    }
}
