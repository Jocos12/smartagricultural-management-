/**
 * Notification.js - Centralized Notification System
 * Manages all system notifications including CRUD operations
 */

class NotificationManager {
    constructor() {
        this.baseUrl = 'http://localhost:1010/api';
        this.notifications = [];
        this.unreadCount = 0;
        this.pollingInterval = null;
        this.pollInterval = 30000; // 30 seconds
        this.isDropdownOpen = false;
        
        this.init();
    }

    init() {
        this.createNotificationDropdown();
        this.cleanupDuplicateNotifications(); // Clean up duplicates first
        this.loadNotificationsFromStorage(); // Load from storage first
        this.loadNotifications(); // Then load from API
        this.startPolling();
        this.attachEventListeners();
    }

    /**
     * Load notifications from localStorage first (for persistence)
     */
    loadNotificationsFromStorage() {
        try {
            const storedEvents = JSON.parse(localStorage.getItem('systemEvents') || '[]');
            
            if (storedEvents.length === 0) {
                console.log('No stored notifications found');
                return;
            }

            const storedNotifications = storedEvents.map(event => ({
                id: event.id || `${event.message}_${event.timestamp}`,
                message: event.message,
                type: event.type || 'SYSTEM',
                timestamp: event.timestamp || new Date().toISOString(),
                read: event.read === true, // Explicitly check for true
                source: event.source || 'system',
                entity: event.entity || null
            }));

            // Remove duplicates based on ID
            const uniqueMap = new Map();
            storedNotifications.forEach(notif => {
                const key = notif.id || `${notif.message}_${notif.timestamp}`;
                if (!uniqueMap.has(key)) {
                    uniqueMap.set(key, notif);
                } else {
                    // If duplicate exists, preserve read status if one is unread
                    const existing = uniqueMap.get(key);
                    if (!existing.read && notif.read) {
                        // Keep the unread one
                    } else if (existing.read && !notif.read) {
                        // Replace with unread one
                        uniqueMap.set(key, notif);
                    }
                }
            });

            this.notifications = Array.from(uniqueMap.values());
            
            // Sort by timestamp (newest first)
            this.notifications.sort((a, b) => {
                const timeA = new Date(a.timestamp || 0);
                const timeB = new Date(b.timestamp || 0);
                return timeB - timeA;
            });

            // Update UI immediately
            this.updateUnreadCount();
            this.renderNotifications();
            
            const unreadCount = this.notifications.filter(n => !n.read).length;
            console.log(`✅ Loaded ${this.notifications.length} notifications from storage, ${unreadCount} unread`);
        } catch (error) {
            console.error('Error loading notifications from storage:', error);
        }
    }

    /**
     * Clean up duplicate notifications from localStorage
     */
    cleanupDuplicateNotifications() {
        try {
            const events = JSON.parse(localStorage.getItem('systemEvents') || '[]');
            const uniqueEvents = [];
            const seen = new Set();

            // Separate unread and read notifications
            const unreadEvents = [];
            const readEvents = [];

            events.forEach(event => {
                // Create unique key from message and timestamp
                const key = `${event.message}_${event.timestamp}`;
                if (!seen.has(key)) {
                    seen.add(key);
                    if (event.read) {
                        readEvents.push(event);
                    } else {
                        unreadEvents.push(event);
                    }
                }
            });

            // Keep all unread notifications, limit read notifications to last 30
            const cleanedReadEvents = readEvents.slice(0, 30);
            const cleanedEvents = [...unreadEvents, ...cleanedReadEvents];
            
            // Sort by timestamp (newest first)
            cleanedEvents.sort((a, b) => {
                const timeA = new Date(a.timestamp || 0);
                const timeB = new Date(b.timestamp || 0);
                return timeB - timeA;
            });

            localStorage.setItem('systemEvents', JSON.stringify(cleanedEvents));
            
            console.log(`Cleaned up notifications: ${events.length} -> ${cleanedEvents.length} (${unreadEvents.length} unread preserved)`);
        } catch (error) {
            console.error('Error cleaning up notifications:', error);
        }
    }

    /**
     * Create notification dropdown HTML structure
     */
    createNotificationDropdown() {
        const notificationBtn = document.querySelector('.notification-btn');
        if (!notificationBtn) {
            console.warn('Notification button not found');
            return;
        }

        // Check if dropdown already exists
        if (document.getElementById('notificationDropdown')) {
            return;
        }

        // Create dropdown container - append to body for fixed positioning
        const dropdown = document.createElement('div');
        dropdown.className = 'notification-dropdown';
        dropdown.id = 'notificationDropdown';
        dropdown.innerHTML = `
            <div class="notification-dropdown-header">
                <h3><i class="fas fa-bell"></i> Notifications</h3>
                <button class="mark-all-read-btn" onclick="notificationManager.markAllAsRead()" title="Mark all as read">
                    <i class="fas fa-check-double"></i> Mark all read
                </button>
            </div>
            <div class="notification-list" id="notificationList">
                <div class="notification-empty">
                    <i class="fas fa-bell-slash"></i>
                    <p>No new notifications</p>
                </div>
            </div>
        `;

        // Append to body for fixed positioning
        document.body.appendChild(dropdown);
    }

    /**
     * Attach event listeners
     */
    attachEventListeners() {
        const notificationBtn = document.querySelector('.notification-btn');
        if (notificationBtn) {
            notificationBtn.addEventListener('click', (e) => {
                e.stopPropagation();
                this.toggleDropdown();
            });
        }

        // Close dropdown when clicking outside
        document.addEventListener('click', (e) => {
            const dropdown = document.getElementById('notificationDropdown');
            if (dropdown && !dropdown.contains(e.target) && 
                !e.target.closest('.notification-btn')) {
                this.closeDropdown();
            }
        });
    }

    /**
     * Toggle notification dropdown
     */
    toggleDropdown() {
        const dropdown = document.getElementById('notificationDropdown');
        if (!dropdown) return;

        this.isDropdownOpen = !this.isDropdownOpen;
        
        if (this.isDropdownOpen) {
            dropdown.classList.add('show');
            this.loadNotifications(); // Refresh when opening
        } else {
            dropdown.classList.remove('show');
        }
    }

    /**
     * Close notification dropdown
     */
    closeDropdown() {
        const dropdown = document.getElementById('notificationDropdown');
        if (dropdown) {
            dropdown.classList.remove('show');
            this.isDropdownOpen = false;
        }
    }

    /**
     * Load notifications from API
     */
    async loadNotifications() {
        try {
            // Try to get user info for authentication
            const userStr = localStorage.getItem('user') || sessionStorage.getItem('user');
            let allNotifications = [];

            if (userStr) {
                try {
                    const user = JSON.parse(userStr);
                    const email = user.email || user.username;

                    // Fetch chat notifications
                    const chatNotifications = await this.fetchChatNotifications(email);
                    
                    // Fetch system notifications
                    const systemNotifications = await this.fetchSystemNotifications();
                    
                    // Combine all notifications
                    allNotifications = [...chatNotifications, ...systemNotifications];
                } catch (error) {
                    console.error('Error parsing user data:', error);
                }
            }

            // If no user, try to get system notifications only
            if (allNotifications.length === 0) {
                allNotifications = await this.fetchSystemNotifications();
            }

            // Create a map of existing notifications to preserve read status
            const existingNotificationsMap = new Map();
            this.notifications.forEach(existing => {
                const key = existing.id ? existing.id.toString() : `${existing.message}_${existing.timestamp}`;
                existingNotificationsMap.set(key, existing);
            });

            // Remove duplicates and merge with existing
            const seenIds = new Set();
            const seenMessages = new Set();
            const uniqueNotifications = [];

            allNotifications.forEach(notif => {
                // Create a unique key from message and timestamp
                const messageKey = `${notif.message}_${notif.timestamp}`;
                const id = notif.id ? notif.id.toString() : messageKey;

                // Skip if we've seen this ID or this exact message+timestamp
                if (!seenIds.has(id) && !seenMessages.has(messageKey)) {
                    seenIds.add(id);
                    seenMessages.add(messageKey);
                    
                    // Ensure ID is set
                    if (!notif.id) {
                        notif.id = id;
                    }
                    
                    // Check if this notification already exists in our array
                    const key = notif.id.toString();
                    const existing = existingNotificationsMap.get(key);
                    
                    if (existing) {
                        // Preserve read status from existing
                        notif.read = existing.read;
                        // Update the existing notification
                        const index = this.notifications.findIndex(n => (n.id ? n.id.toString() : '') === key);
                        if (index !== -1) {
                            this.notifications[index] = { ...this.notifications[index], ...notif };
                        }
                    } else {
                        // New notification
                        uniqueNotifications.push(notif);
                    }
                }
            });

            // Add only new unique notifications to existing array
            uniqueNotifications.forEach(newNotif => {
                const key = newNotif.id ? newNotif.id.toString() : `${newNotif.message}_${newNotif.timestamp}`;
                const exists = this.notifications.some(n => {
                    const nKey = n.id ? n.id.toString() : `${n.message}_${n.timestamp}`;
                    return nKey === key;
                });
                
                if (!exists) {
                    this.notifications.push(newNotif);
                }
            });

            // Sort by timestamp (newest first)
            this.notifications.sort((a, b) => {
                const timeA = new Date(a.timestamp || a.createdAt || 0);
                const timeB = new Date(b.timestamp || b.createdAt || 0);
                return timeB - timeA;
            });

            // Save to localStorage for persistence
            this.saveAllNotificationsToStorage();

            // Update UI
            this.updateUnreadCount();
            this.renderNotifications();
        } catch (error) {
            console.error('Error loading notifications:', error);
            // Don't show error, just use stored notifications
        }
    }

    /**
     * Fetch chat notifications
     */
    async fetchChatNotifications(email) {
        try {
            const token = localStorage.getItem('token') || sessionStorage.getItem('token');
            if (!token || !email) return [];

            const response = await fetch(`${this.baseUrl}/chat/notifications`, {
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                }
            });

            if (response.ok) {
                const data = await response.json();
                return Array.isArray(data) ? data.map(n => ({
                    id: n.id,
                    message: n.content || n.message,
                    type: n.type || 'MESSAGE',
                    timestamp: n.timestamp,
                    read: n.read || false,
                    source: 'chat'
                })) : [];
            }
        } catch (error) {
            console.error('Error fetching chat notifications:', error);
        }
        return [];
    }

    /**
     * Fetch system notifications
     */
    async fetchSystemNotifications() {
        try {
            // Try to fetch from a system notifications endpoint
            // If it doesn't exist, we'll create notifications from system events
            const response = await fetch(`${this.baseUrl}/notifications`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json'
                }
            });

            if (response.ok) {
                const data = await response.json();
                if (Array.isArray(data)) {
                    return data.map(n => ({
                        id: n.id,
                        message: n.message,
                        type: n.type || 'SYSTEM',
                        timestamp: n.timestamp,
                        read: false,
                        source: 'system'
                    }));
                }
            }
        } catch (error) {
            // If endpoint doesn't exist, create notifications from localStorage events
            return this.getNotificationsFromEvents();
        }
        return [];
    }

    /**
     * Get notifications from system events stored in localStorage
     */
    getNotificationsFromEvents() {
        try {
            const events = JSON.parse(localStorage.getItem('systemEvents') || '[]');
            const notifications = [];

            events.forEach(event => {
                if (!event.read) {
                    notifications.push({
                        id: event.id || Date.now() + Math.random(),
                        message: event.message,
                        type: event.type || 'SYSTEM',
                        timestamp: event.timestamp || new Date().toISOString(),
                        read: false,
                        source: 'system'
                    });
                }
            });

            return notifications;
        } catch (error) {
            console.error('Error getting notifications from events:', error);
            return [];
        }
    }

    /**
     * Create a notification for CRUD operations
     */
    createNotification(message, type = 'SYSTEM', entity = null) {
        // Create unique ID based on message and timestamp to avoid duplicates
        const timestamp = new Date().toISOString();
        const uniqueId = `${message}_${timestamp}`;
        
        // Check if this notification already exists (same message within last 3 seconds)
        const recentTime = new Date(Date.now() - 3000).toISOString();
        const duplicate = this.notifications.find(n => {
            const nTime = new Date(n.timestamp || 0);
            const recentTimeDate = new Date(recentTime);
            return n.message === message && 
                   nTime >= recentTimeDate &&
                   !n.read;
        });
        
        if (duplicate) {
            console.log('⚠️ Duplicate notification prevented:', message);
            return; // Don't create duplicate
        }

        const notification = {
            id: uniqueId,
            message: message,
            type: type,
            timestamp: timestamp,
            read: false,
            source: 'system',
            entity: entity
        };

        // Check if notification with same message and recent timestamp already exists
        const existingIndex = this.notifications.findIndex(n => {
            const nTime = new Date(n.timestamp || 0);
            const newTime = new Date(timestamp);
            const timeDiff = Math.abs(newTime - nTime);
            return n.message === message && timeDiff < 3000; // Within 3 seconds
        });

        if (existingIndex === -1) {
            // Add to notifications array
            this.notifications.unshift(notification);
            
            // Sort by timestamp
            this.notifications.sort((a, b) => {
                const timeA = new Date(a.timestamp || 0);
                const timeB = new Date(b.timestamp || 0);
                return timeB - timeA;
            });
            
            // Save to localStorage for persistence
            this.saveNotificationToStorage(notification);
            this.saveAllNotificationsToStorage(); // Also save all to ensure consistency
            
            // Update UI
            this.updateUnreadCount();
            this.renderNotifications();
            
            // Show browser notification if permission granted
            this.showBrowserNotification(message);
            
            console.log(`✅ Notification created: ${message}`);
        } else {
            console.log('⚠️ Notification already exists, skipping');
        }
    }

    /**
     * Save notification to localStorage
     */
    saveNotificationToStorage(notification) {
        try {
            let events = JSON.parse(localStorage.getItem('systemEvents') || '[]');
            
            // Check if notification already exists
            const exists = events.some(e => {
                const eKey = e.id ? e.id.toString() : `${e.message}_${e.timestamp}`;
                const nKey = notification.id ? notification.id.toString() : `${notification.message}_${notification.timestamp}`;
                return eKey === nKey;
            });
            
            if (!exists) {
                events.unshift(notification);
                
                // Keep only last 100 notifications
                if (events.length > 100) {
                    events = events.slice(0, 100);
                }
                
                localStorage.setItem('systemEvents', JSON.stringify(events));
            }
        } catch (error) {
            console.error('Error saving notification:', error);
        }
    }

    /**
     * Save all notifications to localStorage
     */
    saveAllNotificationsToStorage() {
        try {
            // Save all notifications, preserving read status
            const eventsToSave = this.notifications.map(notif => ({
                id: notif.id,
                message: notif.message,
                type: notif.type,
                timestamp: notif.timestamp,
                read: notif.read || false,
                source: notif.source || 'system',
                entity: notif.entity || null
            }));

            // Separate unread and read
            const unreadEvents = eventsToSave.filter(e => !e.read);
            const readEvents = eventsToSave.filter(e => e.read);
            
            // Keep all unread, limit read to last 50
            const limitedReadEvents = readEvents.slice(0, 50);
            const events = [...unreadEvents, ...limitedReadEvents];
            
            // Sort by timestamp
            events.sort((a, b) => {
                const timeA = new Date(a.timestamp || 0);
                const timeB = new Date(b.timestamp || 0);
                return timeB - timeA;
            });
            
            localStorage.setItem('systemEvents', JSON.stringify(events));
        } catch (error) {
            console.error('Error saving all notifications:', error);
        }
    }

    /**
     * Update unread count badge
     */
    updateUnreadCount() {
        // Filter only unread notifications - ensure we count unique notifications only
        const uniqueUnread = new Map();
        
        this.notifications.forEach(n => {
            // Only count if not read
            if (!n.read) {
                // Use ID as key, or create one from message+timestamp
                const key = n.id ? n.id.toString() : `${n.message}_${n.timestamp}`;
                if (!uniqueUnread.has(key)) {
                    uniqueUnread.set(key, n);
                }
            }
        });
        
        this.unreadCount = uniqueUnread.size;
        
        // Try to find badge by ID first, then by class
        const badge = document.getElementById('notificationBadge') || 
                     document.getElementById('notificationCount') ||
                     document.querySelector('.notification-badge');
        if (badge) {
            if (this.unreadCount > 0) {
                // Display exact count, or 99+ if more than 99
                badge.textContent = this.unreadCount > 99 ? '99+' : this.unreadCount.toString();
                badge.style.display = 'flex';
                badge.style.visibility = 'visible';
            } else {
                badge.style.display = 'none';
                badge.style.visibility = 'hidden';
            }
        }
        
        // Log for debugging (only if count > 0 to reduce console noise)
        if (this.unreadCount > 0) {
            console.log(`📬 Notifications: ${this.notifications.length} total, ${this.unreadCount} unread`);
        }
    }

    /**
     * Render notifications in dropdown
     */
    renderNotifications() {
        const list = document.getElementById('notificationList');
        if (!list) return;

        // Filter unread notifications and remove duplicates
        const uniqueUnreadMap = new Map();
        
        this.notifications.forEach(n => {
            if (!n.read) {
                const key = n.id ? n.id.toString() : `${n.message}_${n.timestamp}`;
                if (!uniqueUnreadMap.has(key)) {
                    uniqueUnreadMap.set(key, n);
                }
            }
        });

        const unreadNotifications = Array.from(uniqueUnreadMap.values());

        if (unreadNotifications.length === 0) {
            list.innerHTML = `
                <div class="notification-empty">
                    <i class="fas fa-bell-slash"></i>
                    <p>No new notifications</p>
                    <span style="font-size: 12px; color: var(--text-secondary);">All caught up!</span>
                </div>
            `;
            return;
        }

        list.innerHTML = unreadNotifications.map(notification => {
            const timeAgo = this.getTimeAgo(notification.timestamp);
            const icon = this.getNotificationIcon(notification.type);
            const typeClass = this.getNotificationTypeClass(notification.type);
            const notificationId = notification.id ? notification.id.toString() : `${notification.message}_${notification.timestamp}`;
            const safeId = notificationId.replace(/[^a-zA-Z0-9_]/g, '_');

            return `
                <div class="notification-item ${typeClass}" data-id="${safeId}" onclick="notificationManager.markAsRead('${notificationId}')">
                    <div class="notification-icon">
                        <i class="${icon}"></i>
                    </div>
                    <div class="notification-content">
                        <p class="notification-message">${this.escapeHtml(notification.message)}</p>
                        <span class="notification-time">
                            <i class="fas fa-clock"></i> ${timeAgo}
                        </span>
                    </div>
                    <button class="notification-mark-read" onclick="event.stopPropagation(); notificationManager.markAsRead('${notificationId}')" title="Mark as read">
                        <i class="fas fa-check"></i>
                    </button>
                </div>
            `;
        }).join('');
    }

    /**
     * Mark notification as read
     */
    async markAsRead(notificationId) {
        // Handle both string and number IDs
        const notification = this.notifications.find(n => {
            const nId = n.id ? n.id.toString() : '';
            const searchId = notificationId.toString();
            return nId === searchId || n.id == notificationId;
        });
        
        if (!notification) {
            console.warn('Notification not found:', notificationId);
            return;
        }

        notification.read = true;

        // Update in API if it's a chat notification
        if (notification.source === 'chat') {
            try {
                const token = localStorage.getItem('token') || sessionStorage.getItem('token');
                if (token) {
                    await fetch(`${this.baseUrl}/chat/notifications/${notificationId}/read`, {
                        method: 'POST',
                        headers: {
                            'Authorization': `Bearer ${token}`,
                            'Content-Type': 'application/json'
                        }
                    });
                }
            } catch (error) {
                console.error('Error marking notification as read in API:', error);
            }
        }

        // Update in localStorage immediately
        this.updateNotificationInStorage(notificationId, true);
        this.saveAllNotificationsToStorage(); // Save all to ensure persistence

        // Update UI immediately
        this.updateUnreadCount();
        this.renderNotifications();
        
        // Add visual feedback
        const safeId = notificationId.toString().replace(/[^a-zA-Z0-9_]/g, '_');
        const notificationElement = document.querySelector(`[data-id="${safeId}"]`);
        if (notificationElement) {
            notificationElement.style.opacity = '0.6';
            setTimeout(() => {
                notificationElement.style.opacity = '1';
            }, 300);
        }
    }

    /**
     * Mark all notifications as read
     */
    async markAllAsRead() {
        const unreadNotifications = this.notifications.filter(n => !n.read);
        
        // Mark all as read
        unreadNotifications.forEach(notification => {
            notification.read = true;
        });

        // Save to localStorage
        this.saveAllNotificationsToStorage();

        // Update UI
        this.updateUnreadCount();
        this.renderNotifications();
    }

    /**
     * Update notification in localStorage
     */
    updateNotificationInStorage(notificationId, read) {
        try {
            let events = JSON.parse(localStorage.getItem('systemEvents') || '[]');
            const searchId = notificationId.toString();
            
            events.forEach(event => {
                const eId = event.id ? event.id.toString() : '';
                if (eId === searchId || event.id == notificationId) {
                    event.read = read;
                }
            });
            
            localStorage.setItem('systemEvents', JSON.stringify(events));
        } catch (error) {
            console.error('Error updating notification in storage:', error);
        }
    }

    /**
     * Get time ago string
     */
    getTimeAgo(timestamp) {
        if (!timestamp) return 'Just now';
        
        const now = new Date();
        const time = new Date(timestamp);
        const diff = Math.floor((now - time) / 1000); // seconds

        if (diff < 60) return 'Just now';
        if (diff < 3600) return `${Math.floor(diff / 60)}m ago`;
        if (diff < 86400) return `${Math.floor(diff / 3600)}h ago`;
        if (diff < 604800) return `${Math.floor(diff / 86400)}d ago`;
        
        return time.toLocaleDateString();
    }

    /**
     * Get notification icon based on type
     */
    getNotificationIcon(type) {
        const icons = {
            'MESSAGE': 'fas fa-comment',
            'SYSTEM': 'fas fa-bell',
            'USER_CREATED': 'fas fa-user-plus',
            'USER_UPDATED': 'fas fa-user-edit',
            'USER_DELETED': 'fas fa-user-minus',
            'CROP_CREATED': 'fas fa-seedling',
            'CROP_UPDATED': 'fas fa-edit',
            'CROP_DELETED': 'fas fa-trash',
            'FARM_CREATED': 'fas fa-home',
            'FARM_UPDATED': 'fas fa-edit',
            'FARM_DELETED': 'fas fa-trash',
            'TRANSACTION_CREATED': 'fas fa-exchange-alt',
            'TRANSACTION_UPDATED': 'fas fa-edit',
            'INVENTORY_LOW': 'fas fa-exclamation-triangle',
            'WEATHER_ALERT': 'fas fa-cloud-rain',
            'SYSTEM_ALERT': 'fas fa-exclamation-circle'
        };
        return icons[type] || 'fas fa-bell';
    }

    /**
     * Get notification type class
     */
    getNotificationTypeClass(type) {
        if (type.includes('CREATED')) return 'notification-success';
        if (type.includes('UPDATED')) return 'notification-info';
        if (type.includes('DELETED')) return 'notification-danger';
        if (type.includes('ALERT') || type.includes('LOW')) return 'notification-warning';
        return 'notification-default';
    }

    /**
     * Escape HTML to prevent XSS
     */
    escapeHtml(text) {
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    }

    /**
     * Show browser notification
     */
    showBrowserNotification(message) {
        if ('Notification' in window && Notification.permission === 'granted') {
            new Notification('AgriGuard AI', {
                body: message,
                icon: '/images/logo.jpeg'
            });
        }
    }

    /**
     * Request notification permission
     */
    async requestPermission() {
        if ('Notification' in window && Notification.permission === 'default') {
            await Notification.requestPermission();
        }
    }

    /**
     * Start polling for new notifications
     */
    startPolling() {
        this.pollingInterval = setInterval(() => {
            this.loadNotifications();
        }, this.pollInterval);
    }

    /**
     * Stop polling
     */
    stopPolling() {
        if (this.pollingInterval) {
            clearInterval(this.pollingInterval);
            this.pollingInterval = null;
        }
    }

    /**
     * Show error message
     */
    showError(message) {
        console.error('Notification Error:', message);
        // You can integrate with your toast system here
    }

    /**
     * Create test notifications (for development/testing)
     */
    createTestNotifications() {
        const testNotifications = [
            {
                message: 'New user registered: John Doe',
                type: 'USER_CREATED',
                timestamp: new Date().toISOString()
            },
            {
                message: 'Crop inventory is running low',
                type: 'INVENTORY_LOW',
                timestamp: new Date(Date.now() - 300000).toISOString() // 5 minutes ago
            },
            {
                message: 'Weather alert: Heavy rain expected',
                type: 'WEATHER_ALERT',
                timestamp: new Date(Date.now() - 600000).toISOString() // 10 minutes ago
            }
        ];

        testNotifications.forEach(notif => {
            this.createNotification(notif.message, notif.type, null);
        });
    }

    /**
     * Listen for CRUD operations and create notifications
     */
    listenForCRUDOperations() {
        // Intercept fetch calls to detect CRUD operations
        const originalFetch = window.fetch;
        const self = this;

        window.fetch = async function(...args) {
            const response = await originalFetch.apply(this, args);
            
            // Clone response to read it without consuming
            const clonedResponse = response.clone();
            
            // Check if it's a CRUD operation
            const url = args[0];
            const method = args[1]?.method || 'GET';
            const urlString = typeof url === 'string' ? url : (url?.url || url?.toString() || '');
            
            if (['POST', 'PUT', 'DELETE', 'PATCH'].includes(method) && response.ok) {
                try {
                    const data = await clonedResponse.json();
                    
                    // Determine entity type from URL - more comprehensive matching
                    const entityPatterns = [
                        { pattern: /\/api\/users\/register/i, name: 'user' },
                        { pattern: /\/api\/users\/(?!register)/i, name: 'user' },
                        { pattern: /\/api\/v1\/crops/i, name: 'crop' },
                        { pattern: /\/api\/farms/i, name: 'farm' },
                        { pattern: /\/api\/transactions/i, name: 'transaction' },
                        { pattern: /\/api\/v1\/inventories/i, name: 'inventory' },
                        { pattern: /\/api\/fertilizer-usages/i, name: 'fertilizer usage' },
                        { pattern: /\/api\/irrigation/i, name: 'irrigation' },
                        { pattern: /\/api\/weather-data/i, name: 'weather data' },
                        { pattern: /\/api\/v1\/soil-data/i, name: 'soil data' },
                        { pattern: /\/api\/v1\/crop-productions/i, name: 'crop production' },
                        { pattern: /\/api\/policies/i, name: 'policy' },
                        { pattern: /\/api\/market-prices/i, name: 'market price' },
                        { pattern: /\/api\/supply-chain/i, name: 'supply chain' },
                        { pattern: /\/api\/environmental-data/i, name: 'environmental data' },
                        { pattern: /\/api\/climate-impacts/i, name: 'climate impact' },
                        { pattern: /\/api\/food-security-alerts/i, name: 'food security alert' }
                    ];
                    
                    let entity = 'item';
                    const matchedPattern = entityPatterns.find(ep => ep.pattern.test(urlString));
                    if (matchedPattern) {
                        entity = matchedPattern.name;
                    } else {
                        // Try generic pattern
                        const genericMatch = urlString.match(/\/api\/(?:v1\/)?([^\/\?]+)/i);
                        if (genericMatch) {
                            entity = genericMatch[1].replace(/[-_]/g, ' ');
                        }
                    }
                    
                    // Extract entity name from response if available
                    let entityName = '';
                    if (data && data.data) {
                        if (data.data.fullName) entityName = data.data.fullName;
                        else if (data.data.name) entityName = data.data.name;
                        else if (data.data.title) entityName = data.data.title;
                    } else if (data && data.fullName) {
                        entityName = data.fullName;
                    } else if (data && data.name) {
                        entityName = data.name;
                    }
                    
                    // Create notification message
                    let message = '';
                    let type = 'SYSTEM';
                    
                    if (method === 'POST') {
                        message = entityName 
                            ? `${entity.charAt(0).toUpperCase() + entity.slice(1)} "${entityName}" created successfully`
                            : `${entity.charAt(0).toUpperCase() + entity.slice(1)} created successfully`;
                        type = `${entity.toUpperCase().replace(/\s+/g, '_')}_CREATED`;
                    } else if (method === 'PUT' || method === 'PATCH') {
                        message = entityName 
                            ? `${entity.charAt(0).toUpperCase() + entity.slice(1)} "${entityName}" updated successfully`
                            : `${entity.charAt(0).toUpperCase() + entity.slice(1)} updated successfully`;
                        type = `${entity.toUpperCase().replace(/\s+/g, '_')}_UPDATED`;
                    } else if (method === 'DELETE') {
                        message = entityName 
                            ? `${entity.charAt(0).toUpperCase() + entity.slice(1)} "${entityName}" deleted successfully`
                            : `${entity.charAt(0).toUpperCase() + entity.slice(1)} deleted successfully`;
                        type = `${entity.toUpperCase().replace(/\s+/g, '_')}_DELETED`;
                    }
                    
                    if (message) {
                        // Small delay to ensure the notification is created after the response
                        setTimeout(() => {
                            self.createNotification(message, type, entity);
                        }, 100);
                    }
                } catch (error) {
                    // Response might not be JSON, try to create notification anyway
                    if (response.ok) {
                        const urlString = typeof url === 'string' ? url : (url?.url || '');
                        let entity = 'item';
                        const genericMatch = urlString.match(/\/api\/(?:v1\/)?([^\/\?]+)/i);
                        if (genericMatch) {
                            entity = genericMatch[1].replace(/[-_]/g, ' ');
                        }
                        
                        let message = '';
                        if (method === 'POST') {
                            message = `${entity.charAt(0).toUpperCase() + entity.slice(1)} created successfully`;
                        } else if (method === 'PUT' || method === 'PATCH') {
                            message = `${entity.charAt(0).toUpperCase() + entity.slice(1)} updated successfully`;
                        } else if (method === 'DELETE') {
                            message = `${entity.charAt(0).toUpperCase() + entity.slice(1)} deleted successfully`;
                        }
                        
                        if (message) {
                            setTimeout(() => {
                                self.createNotification(message, 'SYSTEM', entity);
                            }, 100);
                        }
                    }
                }
            }
            
            return response;
        };
    }
}

// Initialize notification manager when DOM is ready
let notificationManager;

// Wait for DOM to be fully loaded
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initializeNotifications);
} else {
    // DOM is already loaded
    initializeNotifications();
}

function initializeNotifications() {
    // Wait a bit to ensure all elements are rendered
    setTimeout(() => {
        try {
            // Check if notificationManager already exists
            if (window.notificationManager) {
                console.log('Notification manager already initialized');
                return;
            }

            notificationManager = new NotificationManager();
            notificationManager.requestPermission();
            notificationManager.listenForCRUDOperations();
            
            // Export for global access
            window.notificationManager = notificationManager;
            
            // Force update of badge immediately and periodically
            const updateBadge = () => {
                if (notificationManager) {
                    notificationManager.updateUnreadCount();
                    if (notificationManager.isDropdownOpen) {
                        notificationManager.renderNotifications();
                    }
                }
            };
            
            // Update immediately
            setTimeout(updateBadge, 500);
            
            // Update again after 1 second to ensure badge is visible
            setTimeout(updateBadge, 1000);
            
            // Update every 5 seconds to catch any missed notifications
            setInterval(updateBadge, 5000);
            
            console.log('Notification system initialized successfully');
        } catch (error) {
            console.error('Error initializing notification system:', error);
        }
    }, 300);
}
