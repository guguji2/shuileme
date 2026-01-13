<template>
    <view class="container">
        <view class="card">
            <view class="section-title">睡眠设置</view>
            <view class="form-item">
                <text class="label">睡觉截止时间</text>
                <picker mode="time" :value="deadlineTime" @change="onTimeChange">
                    <view class="picker">{{ deadlineTime || '请选择时间' }}</view>
                </picker>
            </view>
            <button class="save-btn" @click="saveSleepSetting">保存设置</button>
        </view>

        <view class="card">
            <view class="section-header">
                <text class="section-title">紧急联系人</text>
                <button class="add-btn" size="mini" @click="showAddContactModal">添加</button>
            </view>
            <view class="contact-list" v-if="contacts.length > 0">
                <view class="contact-item" v-for="contact in contacts" :key="contact.id">
                    <view class="contact-info">
                        <text class="contact-name">{{ contact.name }}</text>
                        <text class="contact-email">{{ contact.email }}</text>
                    </view>
                    <button class="delete-btn" size="mini" @click="deleteContact(contact.id)">删除</button>
                </view>
            </view>
            <view class="empty-tip" v-else>
                <text>暂无紧急联系人</text>
            </view>
        </view>

        <button class="logout-btn" @click="handleLogout">退出登录</button>

        <view class="modal-mask" v-if="showModal" @click="closeModal">
            <view class="modal-content" @click.stop>
                <view class="modal-title">添加紧急联系人</view>
                <view class="modal-form">
                    <view class="form-item">
                        <text class="form-label">姓名</text>
                        <input class="form-input" v-model="contactName" placeholder="请输入姓名" />
                    </view>
                    <view class="form-item">
                        <text class="form-label">邮箱</text>
                        <input class="form-input" v-model="contactEmail" placeholder="请输入邮箱地址" />
                    </view>
                </view>
                <view class="modal-buttons">
                    <button class="modal-btn cancel-btn" @click="closeModal">取消</button>
                    <button class="modal-btn confirm-btn" @click="confirmAddContact">确定</button>
                </view>
            </view>
        </view>
    </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '@/api/index.js'

const deadlineTime = ref('')
const contacts = ref([])
const user = ref(null)
const showModal = ref(false)
const contactName = ref('')
const contactEmail = ref('')

onMounted(() => {
    loadData()
})

function loadData() {
    user.value = uni.getStorageSync('user')
    if (!user.value || !user.value.id) {
        uni.navigateTo({ url: '/pages/login/login' })
        return
    }

    loadSettings()
    loadContacts()
}

async function loadSettings() {
    try {
        const res = await api.getSleepSetting(user.value.id)
        if (res.code === 200 && res.data) {
            deadlineTime.value = res.data.deadlineTime
        }
    } catch (e) {
        console.error('加载设置失败', e)
    }
}

async function loadContacts() {
    try {
        const res = await api.getContacts(user.value.id)
        if (res.code === 200) {
            contacts.value = res.data || []
        }
    } catch (e) {
        console.error('加载联系人失败', e)
    }
}

function onTimeChange(e) {
    deadlineTime.value = e.detail.value
}

async function saveSleepSetting() {
    if (!deadlineTime.value) {
        uni.showToast({
            title: '请选择睡觉时间',
            icon: 'none'
        })
        return
    }

    try {
        uni.showLoading({ title: '保存中...' })

        const res = await api.saveSleepSetting({
            userId: user.value.id,
            deadlineTime: deadlineTime.value
        })

        uni.hideLoading()

        if (res.code === 200) {
            uni.showToast({
                title: '保存成功',
                icon: 'success'
            })
        } else {
            uni.showToast({
                title: '保存失败',
                icon: 'none'
            })
        }
    } catch (e) {
        uni.hideLoading()
        console.error('保存失败', e)
        uni.showToast({
            title: '网络错误',
            icon: 'none'
        })
    }
}

function showAddContactModal() {
    showModal.value = true
    contactName.value = ''
    contactEmail.value = ''
}

function closeModal() {
    showModal.value = false
    contactName.value = ''
    contactEmail.value = ''
}

async function confirmAddContact() {
    if (!contactName.value || !contactEmail.value) {
        uni.showToast({
            title: '请填写完整信息',
            icon: 'none'
        })
        return
    }

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
    if (!emailRegex.test(contactEmail.value)) {
        uni.showToast({
            title: '邮箱格式不正确',
            icon: 'none'
        })
        return
    }

    try {
        uni.showLoading({ title: '添加中...' })

        const res = await api.addContact({
            userId: user.value.id,
            name: contactName.value,
            email: contactEmail.value
        })

        uni.hideLoading()

        if (res.code === 200) {
            contacts.value.push(res.data)
            uni.showToast({
                title: '添加成功',
                icon: 'success'
            })
            closeModal()
        } else {
            uni.showToast({
                title: '添加失败',
                icon: 'none'
            })
        }
    } catch (e) {
        uni.hideLoading()
        console.error('添加失败', e)
        uni.showToast({
            title: '网络错误',
            icon: 'none'
        })
    }
}

async function deleteContact(contactId) {
    uni.showModal({
        title: '确认删除',
        content: '确定要删除该联系人吗？',
        success: async (res) => {
            if (res.confirm) {
                try {
                    uni.showLoading({ title: '删除中...' })

                    await api.deleteContact(contactId)

                    uni.hideLoading()

                    contacts.value = contacts.value.filter(c => c.id !== contactId)

                    uni.showToast({
                        title: '删除成功',
                        icon: 'success'
                    })
                } catch (e) {
                    uni.hideLoading()
                    console.error('删除失败', e)
                    uni.showToast({
                        title: '删除失败',
                        icon: 'none'
                    })
                }
            }
        }
    })
}

function handleLogout() {
    uni.removeStorageSync('user')
    uni.removeStorageSync('token')
    uni.reLaunch({ url: '/pages/login/login' })
}
</script>

<style scoped>
.section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 30rpx;
}

.section-title {
    font-size: 32rpx;
    font-weight: bold;
    color: #333;
}

.form-item {
    margin-bottom: 30rpx;
}

.label {
    display: block;
    font-size: 28rpx;
    color: #666;
    margin-bottom: 15rpx;
}

.picker {
    background: #f5f5f5;
    padding: 20rpx;
    border-radius: 12rpx;
    font-size: 28rpx;
}

.save-btn {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: #fff;
    border-radius: 50rpx;
    margin-top: 20rpx;
}

.add-btn {
    background: #667eea;
    color: #fff;
}

.contact-list {
    margin-top: 20rpx;
}

.contact-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 20rpx 0;
    border-bottom: 1rpx solid #eee;
}

.contact-item:last-child {
    border-bottom: none;
}

.contact-info {
    flex: 1;
    display: flex;
    flex-direction: column;
}

.contact-name {
    font-size: 30rpx;
    color: #333;
    font-weight: 500;
    margin-bottom: 8rpx;
}

.contact-email {
    font-size: 24rpx;
    color: #999;
}

.delete-btn {
    background: #ff4d4f;
    color: #fff;
}

.empty-tip {
    text-align: center;
    padding: 40rpx 0;
    color: #999;
}

.logout-btn {
    background: #fff;
    color: #ff4d4f;
    border: 1rpx solid #ff4d4f;
    margin-top: 20rpx;
}

.modal-mask {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(0, 0, 0, 0.5);
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 9999;
}

.modal-content {
    background: #fff;
    width: 80%;
    border-radius: 20rpx;
    padding: 40rpx;
}

.modal-title {
    font-size: 36rpx;
    font-weight: bold;
    color: #333;
    text-align: center;
    margin-bottom: 40rpx;
}

.modal-form {
    margin-bottom: 40rpx;
}

.modal-form .form-item {
    margin-bottom: 30rpx;
}

.form-label {
    display: block;
    font-size: 28rpx;
    color: #666;
    margin-bottom: 15rpx;
}

.form-input {
    width: 100%;
    background: #f5f5f5;
    padding: 20rpx;
    border-radius: 12rpx;
    font-size: 28rpx;
    box-sizing: border-box;
}

.modal-buttons {
    display: flex;
    gap: 20rpx;
}

.modal-btn {
    flex: 1;
    padding: 20rpx;
    border-radius: 12rpx;
    font-size: 28rpx;
    border: none;
}

.cancel-btn {
    background: #f5f5f5;
    color: #666;
}

.confirm-btn {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: #fff;
}
</style>
