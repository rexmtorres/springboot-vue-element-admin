<template>
  <div class="app-container">
    <el-table
      v-loading="listLoading"
      :data="list"
      border
      style="width: 100%"
    >
      <el-table-column
        prop="username"
        label="username"
      />
      <el-table-column
        prop="nickname"
        label="nickname"
      />
      <el-table-column
        prop="loginTime"
        label="loginTime"
      />
      <el-table-column
        label="operations"
      >
        <template slot-scope="scope">
          <el-button
            type="text"
            size="small"
            @click="handleForceLogout(scope.row)"
          >Log Out
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
import { forceLogout, getAllOnlineUsers } from '@/api/online-user'

export default {
  name: 'OnlineUsers',
  data() {
    return {
      list: null,
      listLoading: true
    }
  },

  created() {
    this.getList()
  },
  methods: {
    // 获取列表
    async getList() {
      this.listLoading = true
      const { data } = await getAllOnlineUsers()
      this.list = data
      this.listLoading = false
    },
    // 强制退出
    handleForceLogout(row) {
      this.$confirm('Do you want to confirm the operation?', 'Prompt', {
        confirmButtonText: 'Confirm',
        cancelButtonText: 'Cancel',
        type: 'warning'
      }).then(() => {
        forceLogout(row.loginName).then(() => {
          this.$confirm('Successful Operation', 'Prompt', {
            showClose: false,
            showCancelButton: false,
            closeOnClickModal: false,
            type: 'success'
          }).then(_ => {
            this.getList()
          })
        }).catch(() => {
        })
      }).catch(() => {

      })
    }

  }
}
</script>

<style scoped>

</style>
