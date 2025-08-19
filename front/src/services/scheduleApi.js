const API_BASE_URL = 'http://localhost:8080/api';

export const scheduleApi = {
  /**
   * 월별 일정 조회
   * @param {number} year - 년도
   * @param {number} month - 월 (1-12)
   * @returns {Promise<Object>} 일정 목록
   */
  async getMonthlySchedules(year, month) {
    try {
      const response = await fetch(`${API_BASE_URL}/schedules/calendar`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          year: year,
          month: month
        })
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      return await response.json();
    } catch (error) {
      console.error('일정 조회 중 오류 발생:', error);
      throw error;
    }
  }
};
