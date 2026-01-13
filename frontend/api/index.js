import request from './request'

export default {
    // 认证相关
    register(data) {
        return request.post('/auth/register', data)
    },
    login(data) {
        return request.post('/auth/login', data)
    },

    // 打卡相关
    checkIn(data) {
        return request.post('/checkin', data)
    },
    getCheckInStatus(userId) {
        return request.get('/checkin/status/' + userId)
    },

    // 设置相关
    saveSleepSetting(data) {
        return request.post('/settings/sleep', data)
    },
    getSleepSetting(userId) {
        return request.get('/settings/sleep/' + userId)
    },
    addContact(data) {
        return request.post('/settings/contacts', data)
    },
    getContacts(userId) {
        return request.get('/settings/contacts/' + userId)
    },
    deleteContact(contactId) {
        return request.delete('/settings/contacts/' + contactId)
    }
}
