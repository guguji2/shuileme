<template>
    <view class="overview-card">
        <view class="stat-item">
            <text class="stat-value">{{ totalDays || 0 }}</text>
            <text class="stat-label">累计睡觉</text>
        </view>
        <view class="stat-item">
            <text class="stat-value">{{ currentStreak || 0 }}</text>
            <text class="stat-label">连续睡觉</text>
        </view>
        <view class="stat-item">
            <text class="stat-value">{{ monthlyDays || 0 }}</text>
            <text class="stat-label">本月睡觉</text>
        </view>
    </view>
</template>

<script setup>
defineProps({
    totalDays: {
        type: Number,
        default: 0
    },
    currentStreak: {
        type: Number,
        default: 0
    },
    monthlyDays: {
        type: Number,
        default: 0
    }
})
</script>

<style scoped>
.overview-card {
    display: flex;
    justify-content: space-around;
    background: #fff;
    border-radius: 20rpx;
    padding: 40rpx 20rpx;
    margin-bottom: 20rpx;
    box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.1);
}

.stat-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    flex: 1;
}

.stat-value {
    font-size: 48rpx;
    font-weight: bold;
    color: #667eea;
    margin-bottom: 10rpx;
}

.stat-label {
    font-size: 24rpx;
    color: #999;
}
</style>
