<template>
    <view class="tab-bar">
        <view
            class="tab-item"
            :class="{ active: activeTab === 'weekly' }"
            @click="handleTabChange('weekly')"
        >
            <text class="tab-text">周视图</text>
        </view>
        <view
            class="tab-item"
            :class="{ active: activeTab === 'monthly' }"
            @click="handleTabChange('monthly')"
        >
            <text class="tab-text">月度热力图</text>
        </view>
    </view>
</template>

<script setup>
defineProps({
    activeTab: {
        type: String,
        default: 'weekly'
    }
})

const emit = defineEmits(['change'])

function handleTabChange(tab) {
    emit('change', tab)
}
</script>

<style scoped>
.tab-bar {
    display: flex;
    background: #fff;
    border-radius: 20rpx;
    padding: 10rpx;
    margin-bottom: 20rpx;
    box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.1);
}

.tab-item {
    flex: 1;
    text-align: center;
    padding: 20rpx;
    border-radius: 16rpx;
    transition: all 0.3s ease;
}

.tab-item.active {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.tab-text {
    font-size: 28rpx;
    color: #666;
}

.tab-item.active .tab-text {
    color: #fff;
    font-weight: bold;
}
</style>
