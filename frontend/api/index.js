import request from './request'

export default {
    // 认证相关
    register(data) {
        return request.post('/auth/register', data)
    },
    login(data) {
        return request.post('/auth/login', data)
    },
    logout(data) {
        return request.post('/auth/logout', data)
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
    },

    // 统计相关
    getOverviewStats(userId) {
        return request.get('/stats/overview/' + userId)
    },
    getWeeklyStats(userId) {
        return request.get('/stats/weekly/' + userId)
    },
    getMonthlyStats(userId, year, month) {
        let url = '/stats/monthly/' + userId
        const params = []
        if (year) params.push('year=' + year)
        if (month) params.push('month=' + month)
        if (params.length > 0) url += '?' + params.join('&')
        return request.get(url)
    }
}
