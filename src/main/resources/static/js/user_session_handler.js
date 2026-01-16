// user_session_handler.js - AgriGuard AI Version
class UserSessionHandler {
    static instance = null;

    constructor() {
        // Singleton pattern - only create if instance doesn't exist
        if (UserSessionHandler.instance) {
            console.warn('UserSessionHandler already instantiated');
            return UserSessionHandler.instance;
        }


// Configuration
this.config = {
    apiBaseUrl: 'http://localhost:1010/api/users',
    defaultAvatarPath: 'https://ui-avatars.com/api/?name=User&background=667eea&color=fff&size=200&bold=true',
    storageKeys: {
        token: 'agriguard_jwt_token',
        userInfo: 'agriguard_user_info',
        role: 'agriguard_userRole',
        lastActivity: 'agriguard_last_activity'
    },
    sessionTimeout: 30 * 60 * 1000 // 30 minutes
};

        // Session properties
        this.session = null;
        this.tokenRefreshTimer = null;
        this.sessionKey = 'agriguard_userSession';
        this.resetEmailKey = 'agriguard_resetEmail';

        this.initializeActivityTracker();
        UserSessionHandler.instance = this;
    }

    static getInstance() {
        if (!UserSessionHandler.instance) {
            UserSessionHandler.instance = new UserSessionHandler();
        }
        return UserSessionHandler.instance;
    }

    // Static method to save user session data
    static saveUserSession(loginData) {
        const instance = UserSessionHandler.getInstance();
        return instance._saveUserSession(loginData);
    }

    // Private method to save user session data to localStorage
    _saveUserSession(loginData) {
        if (!loginData || !loginData.token) {
            console.error('Invalid login data provided');
            return false;
        }

        try {
            // Validate required fields
            if (!loginData.email) {
                console.error('Missing email in login data');
                return false;
            }

            // Save token
            localStorage.setItem(this.config.storageKeys.token, loginData.token);

            // Build complete user info object
            const userInfo = {
                userId: loginData.userId || loginData.id || loginData.staffId,
                id: loginData.id || loginData.userId || loginData.staffId,
                fullName: loginData.fullName || loginData.username || 'User',
                username: loginData.username || loginData.email,
                email: loginData.email,
                phoneNumber: loginData.phoneNumber || '',
                role: loginData.role || 'USER',
                photo: loginData.photo || loginData.profileImageUrl || null,
                photoBase64: loginData.photoBase64 || loginData.photo || null,
                profileImageUrl: loginData.profileImageUrl || loginData.photo || null,
                isActive: loginData.isActive !== undefined ? loginData.isActive : true,
                lastLogin: new Date().toISOString(),
                createdAt: loginData.createdAt || null,
                updatedAt: loginData.updatedAt || null,
                token: loginData.token,
                sessionTimestamp: new Date().toISOString(),
                expiresAt: new Date(Date.now() + 24 * 60 * 60 * 1000).toISOString() // 24 hours
            };

            // Ensure userId is present
            if (!userInfo.userId) {
                console.error('User ID is missing from login data');
                return false;
            }

            // Save to localStorage with multiple keys for compatibility
            localStorage.setItem(this.config.storageKeys.userInfo, JSON.stringify(userInfo));
            localStorage.setItem(this.config.storageKeys.role, userInfo.role);
            localStorage.setItem(this.sessionKey, JSON.stringify(userInfo));

            // Update activity timestamp
            this.updateLastActivity();

            // Update session property
            this.session = userInfo;

            console.log('Session saved successfully for:', userInfo.email);
            console.log('User ID:', userInfo.userId);
            console.log('Has photo:', !!userInfo.photo);

            return true;
        } catch (error) {
            console.error('Error saving session:', error);
            return false;
        }
    }

    // Enhanced saveUserSession method for new functionality
    saveUserSession(userData) {
        if (!userData || !userData.token) {
            console.error('Invalid user data for session');
            return false;
        }

        // Add session timestamp and expiration
        userData.sessionTimestamp = new Date().toISOString();
        userData.expiresAt = new Date(Date.now() + 24 * 60 * 60 * 1000).toISOString(); // 24 hours

        this.session = userData;

        try {
            localStorage.setItem(this.sessionKey, JSON.stringify(userData));
            // Also save in the old format for compatibility
            if (userData.email) {
                localStorage.setItem(this.config.storageKeys.userInfo, JSON.stringify(userData));
                localStorage.setItem(this.config.storageKeys.token, userData.token);
                localStorage.setItem(this.config.storageKeys.role, userData.role || 'USER');
            }
            return true;
        } catch (e) {
            console.error('Error saving user session:', e);
            return false;
        }
    }

    // Get user session with validation
    getUserSession() {
        if (this.session && this.isSessionValid(this.session)) {
            return this.session;
        }

        try {
            const sessionData = localStorage.getItem(this.sessionKey);
            if (sessionData) {
                const parsedSession = JSON.parse(sessionData);
                if (this.isSessionValid(parsedSession)) {
                    this.session = parsedSession;
                    return this.session;
                } else {
                    this.clearUserSession();
                    return null;
                }
            }
        } catch (e) {
            console.error('Error retrieving user session:', e);
            this.clearUserSession();
        }
        return null;
    }

    // Check if session is valid
    isSessionValid(session) {
        if (!session || !session.token) {
            return false;
        }

        // Check expiration if expiresAt is present
        if (session.expiresAt) {
            const expirationTime = new Date(session.expiresAt);
            const currentTime = new Date();
            return currentTime < expirationTime;
        }

        // Fallback to activity-based validation
        const lastActivity = localStorage.getItem(this.config.storageKeys.lastActivity);
        if (lastActivity) {
            const timeSinceLastActivity = Date.now() - parseInt(lastActivity);
            return timeSinceLastActivity < this.config.sessionTimeout;
        }

        return true; // If no expiration info, assume valid
    }

    // Get current logged-in user
    getCurrentUser() {
        try {
            // First try to get from session
            const sessionUser = this.getUserSession();
            if (sessionUser) {
                return sessionUser;
            }

            // Try to get from userInfo
            const userInfoStr = localStorage.getItem(this.config.storageKeys.userInfo);
            if (userInfoStr) {
                const userData = JSON.parse(userInfoStr);
                if (userData.userId && userData.email) {
                    return userData;
                }
            }

            return null;
        } catch (error) {
            console.error('Error getting current user:', error);
            return null;
        }
    }

    // Load user profile data
    loadUserProfile() {
        return this.getCurrentUser();
    }




// Update UI with user profile information
updateProfileUI() {
    const userProfile = this.loadUserProfile();
    if (!userProfile) {
        console.warn('No user profile found for UI update');
        return;
    }

    console.log('Updating profile UI for:', userProfile.fullName);

    // Update profile image
    const profileImages = document.querySelectorAll('.profile-image img, .user-avatar, .profile-avatar');
    profileImages.forEach(img => {
        if (userProfile.photo || userProfile.photoBase64 || userProfile.profileImageUrl) {
            const photoData = userProfile.photo || userProfile.photoBase64 || userProfile.profileImageUrl;

            // Check if it's already a data URL or HTTP URL
            if (photoData.startsWith('data:image/') || photoData.startsWith('http')) {
                img.src = photoData;
            } else {
                // Assume it's base64 and add the data URL prefix
                img.src = `data:image/jpeg;base64,${photoData}`;
            }

            // Fallback avec avatar généré
            img.onerror = () => {
                console.warn('Failed to load profile image, using generated avatar');
                const userName = userProfile.fullName || userProfile.username || 'User';
                img.src = `https://ui-avatars.com/api/?name=${encodeURIComponent(userName)}&background=667eea&color=fff&size=200&bold=true`;
            };
        } else {
            // Pas de photo, utiliser un avatar généré avec le nom
            const userName = userProfile.fullName || userProfile.username || 'User';
            img.src = `https://ui-avatars.com/api/?name=${encodeURIComponent(userName)}&background=667eea&color=fff&size=200&bold=true`;
        }
    });

    // Update name elements
    const nameElements = document.querySelectorAll('.user-name, .profile-name, .staff-name');
    nameElements.forEach(element => {
        element.textContent = userProfile.fullName;
    });

    // Update email elements
    const emailElements = document.querySelectorAll('.user-email, .profile-email, .staff-email');
    emailElements.forEach(element => {
        element.textContent = userProfile.email;
    });

    // Update role elements
    const roleElements = document.querySelectorAll('.user-role, .profile-role, .staff-role');
    roleElements.forEach(element => {
        element.textContent = this.formatRole(userProfile.role);
    });

    // Update phone elements
    const phoneElements = document.querySelectorAll('.user-phone, .profile-phone, .staff-phone');
    phoneElements.forEach(element => {
        element.textContent = userProfile.phoneNumber || 'N/A';
    });
}

    // Format role string to be more readable
    formatRole(role) {
        if (!role) return 'User';
        return role.split('_')
            .map(word => word.charAt(0).toUpperCase() + word.slice(1).toLowerCase())
            .join(' ');
    }

    // Get authentication token
    getAuthToken() {
        return localStorage.getItem(this.config.storageKeys.token);
    }



getToken() {
    return this.getAuthToken();
}


    // Check if user is authenticated and session is not expired
    isAuthenticated() {
        const token = this.getAuthToken();
        const session = this.getUserSession();

        if (!token) return false;

        if (session) {
            return this.isSessionValid(session);
        }

        // Fallback to activity-based check
        const lastActivity = localStorage.getItem(this.config.storageKeys.lastActivity);
        if (!lastActivity) return false;

        const timeSinceLastActivity = Date.now() - parseInt(lastActivity);
        return timeSinceLastActivity < this.config.sessionTimeout;
    }

    // Check if user is logged in (alias for compatibility)
    isLoggedIn() {
        return this.isAuthenticated();
    }

    // Update last activity timestamp
    updateLastActivity() {
        localStorage.setItem(this.config.storageKeys.lastActivity, Date.now().toString());
    }

    // Initialize activity tracking
    initializeActivityTracker() {
        const events = ['click', 'keypress', 'mousemove', 'touchstart', 'scroll'];

        events.forEach(eventType => {
            document.addEventListener(eventType, () => {
                if (this.isAuthenticated()) {
                    this.updateLastActivity();
                }
            }, { passive: true });
        });

        // Check session status periodically
        setInterval(() => {
            if (!this.isAuthenticated() && this.getAuthToken()) {
                console.warn('Session expired');
                // Optionally redirect to login or show warning
            }
        }, 60000); // Check every minute
    }

    // Refresh session token
    async refreshSession() {
        try {
            const token = this.getAuthToken();
            if (!token) throw new Error('No authentication token found');

            const response = await fetch(`${this.config.apiBaseUrl}/refresh-token`, {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                }
            });

            if (!response.ok) throw new Error('Failed to refresh session');

            const data = await response.json();
            if (data.token) {
                localStorage.setItem(this.config.storageKeys.token, data.token);
                this.updateLastActivity();

                // Update session data
                if (this.session) {
                    this.session.token = data.token;
                    this.session.expiresAt = new Date(Date.now() + 24 * 60 * 60 * 1000).toISOString();
                    localStorage.setItem(this.sessionKey, JSON.stringify(this.session));
                }

                return true;
            }
            return false;
        } catch (error) {
            console.error('Error refreshing session:', error);
            return false;
        }
    }

    // Clear all session data
    clearSession() {
        Object.values(this.config.storageKeys).forEach(key => {
            localStorage.removeItem(key);
        });
        localStorage.removeItem(this.sessionKey);
        localStorage.removeItem(this.resetEmailKey);

        this.session = null;

        if (this.tokenRefreshTimer) {
            clearInterval(this.tokenRefreshTimer);
            this.tokenRefreshTimer = null;
        }

        console.log('Session cleared');
    }

    // Clear user session (alias for compatibility)
    clearUserSession() {
        this.clearSession();
    }

    // Logout user
    logout() {
        const currentUser = this.getCurrentUser();
        if (currentUser && window.sessionTracker) {
            window.sessionTracker.endSession(currentUser.userId || currentUser.id);
        }

        this.clearSession();
        window.location.href = '/login.html';
    }

    // Get user role
    getUserRole() {
        const user = this.getCurrentUser();
        return user ? user.role : null;
    }

    // Check if user has specific role
    hasRole(role) {
        const userRole = this.getUserRole();
        return userRole === role;
    }

    // Check if user has any of the specified roles
    hasAnyRole(roles) {
        const userRole = this.getUserRole();
        return roles.includes(userRole);
    }

    // Update user profile data
    updateUserProfile(updates) {
        try {
            const currentProfile = this.loadUserProfile();
            if (!currentProfile) return false;

            const updatedProfile = { ...currentProfile, ...updates };

            // Update in all storage locations
            localStorage.setItem(this.config.storageKeys.userInfo, JSON.stringify(updatedProfile));
            localStorage.setItem(this.sessionKey, JSON.stringify(updatedProfile));

            // Update session property
            this.session = updatedProfile;

            // Update UI if needed
            this.updateProfileUI();

            return true;
        } catch (error) {
            console.error('Error updating profile:', error);
            return false;
        }
    }

    // Save reset email for password reset functionality
    saveResetEmail(email) {
        try {
            localStorage.setItem(this.resetEmailKey, email);
            return true;
        } catch (e) {
            console.error('Error saving reset email:', e);
            return false;
        }
    }

    // Get reset email
    getResetEmail() {
        try {
            return localStorage.getItem(this.resetEmailKey);
        } catch (e) {
            console.error('Error getting reset email:', e);
            return null;
        }
    }

    // Clear reset email
    clearResetEmail() {
        try {
            localStorage.removeItem(this.resetEmailKey);
        } catch (e) {
            console.error('Error clearing reset email:', e);
        }
    }
}

// Utility function to show error messages
function showError(message) {
    const errorElement = document.getElementById('error-message');
    if (errorElement) {
        errorElement.textContent = message;
        errorElement.style.display = 'block';

        // Auto-hide after 5 seconds
        setTimeout(() => {
            errorElement.style.display = 'none';
        }, 5000);
    } else {
        console.error('Error:', message);
        alert(message); // Fallback
    }
}

// Function to show success messages
function showSuccess(message) {
    const successElement = document.getElementById('success-message');
    if (successElement) {
        successElement.textContent = message;
        successElement.style.display = 'block';

        // Auto-hide after 3 seconds
        setTimeout(() => {
            successElement.style.display = 'none';
        }, 3000);
    } else {
        console.log('Success:', message);
    }
}

// Handle successful login process
async function handleSuccessfulLogin(data) {
    try {
        console.log('Processing login response:', data);

        if (!data || !data.success || !data.token || !data.user) {
            throw new Error('Invalid server response format');
        }

        // Prepare user data with photo handling
        const userData = {
            userId: data.user.id,
            id: data.user.id,
            email: data.user.email,
            username: data.user.username,
            fullName: data.user.fullName || data.user.username,
            phoneNumber: data.user.phoneNumber,
            role: data.user.role || 'USER',
            isActive: data.user.isActive !== undefined ? data.user.isActive : true,
            createdAt: data.user.createdAt,
            updatedAt: data.user.updatedAt,
            lastLogin: data.user.lastLogin,
            token: data.token
        };

        // Handle photo data
        if (data.user.profileImageUrl) {
            userData.photo = data.user.profileImageUrl;
            userData.photoBase64 = data.user.profileImageUrl;
            userData.profileImageUrl = data.user.profileImageUrl;
        }

        console.log('Prepared user data:', {
            ...userData,
            photo: userData.photo ? 'Present' : 'Not present'
        });

        // Save session
        const loginSuccess = UserSessionHandler.saveUserSession(userData);
        if (!loginSuccess) {
            throw new Error('Failed to save session data');
        }

        // Record login in session tracker
        if (window.sessionTracker) {
            window.sessionTracker.recordLogin(userData, true);
        } else {
            console.warn('SessionTracker is not initialized');
        }

        // Show success message
        showSuccess('Login successful! Redirecting...');

        // Determine redirect URL based on role
        const redirectUrls = {
            'FARMER': '/farmer-dashboard.html',
            'BUYER': '/buyer-dashboard.html',
            'ADMIN': '/admin-dashboard.html',
            'ANALYST': '/analyst-dashboard.html',
            'GOVERNMENT': '/government-dashboard.html'
        };

        const redirectPage = redirectUrls[userData.role] || '/dashboard.html';

        // Small delay to show success message
        setTimeout(() => {
            window.location.href = redirectPage;
        }, 1000);

    } catch (error) {
        console.error('Login process error:', error);

        // Record failed login if tracker is available
        if (window.sessionTracker && data?.user) {
            window.sessionTracker.recordLogin(data.user, false);
        }

        showError(error.message || 'Login failed. Please try again.');
    }
}

// Initialize on page load
document.addEventListener('DOMContentLoaded', function() {
    // Initialize session handler
    const sessionHandler = UserSessionHandler.getInstance();

    // Update profile UI if user is logged in
    if (sessionHandler.isAuthenticated()) {
        sessionHandler.updateProfileUI();
    }

    // Set up logout buttons
    const logoutButtons = document.querySelectorAll('.logout-btn, .btn-logout');
    logoutButtons.forEach(button => {
        button.addEventListener('click', function(e) {
            e.preventDefault();
            sessionHandler.logout();
        });
    });
});

// Expose to global scope
if (typeof window !== 'undefined') {
    window.UserSessionHandler = UserSessionHandler;
    window.handleSuccessfulLogin = handleSuccessfulLogin;
    window.showError = showError;
    window.showSuccess = showSuccess;
}

// Export for Node.js environments
if (typeof module !== 'undefined' && module.exports) {
    module.exports = {
        UserSessionHandler,
        handleSuccessfulLogin,
        showError,
        showSuccess
    };
}
