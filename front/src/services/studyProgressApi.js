const API_BASE_URL = 'http://localhost:8080/api';

export const studyProgressApi = {
  /**
   * 현재 진행 중인 스터디 진행 현황 조회
   * @returns {Promise<Object>} 스터디 진행 현황 데이터
   */
  async getCurrentStudyProgress() {
    try {
      const response = await fetch(`${API_BASE_URL}/study-progress/current`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
        }
      });

      if (!response.ok) {
        const errorData = await response.json().catch(() => ({}));
        const error = new Error(errorData.message || `HTTP error! status: ${response.status}`);
        error.status = response.status;
        error.data = errorData;
        throw error;
      }

      return await response.json();
    } catch (error) {
      console.error('현재 스터디 진행 현황 조회 중 오류 발생:', error);
      throw error;
    }
  },

  /**
   * 특정 스터디 진행 현황 조회
   * @param {number} studyId - 스터디 ID
   * @returns {Promise<Object>} 스터디 진행 현황 데이터
   */
  async getStudyProgress(studyId) {
    try {
      const response = await fetch(`${API_BASE_URL}/study-progress/${studyId}`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
        }
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      return await response.json();
    } catch (error) {
      console.error('스터디 진행 현황 조회 중 오류 발생:', error);
      throw error;
    }
  },

  /**
   * 스터디 진행 현황 조회 (POST 방식)
   * @param {Object} requestData - 요청 데이터 { studyId: number }
   * @returns {Promise<Object>} 스터디 진행 현황 데이터
   */
  async getStudyProgressByPost(requestData) {
    try {
      const response = await fetch(`${API_BASE_URL}/study-progress`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(requestData)
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      return await response.json();
    } catch (error) {
      console.error('스터디 진행 현황 조회 중 오류 발생:', error);
      throw error;
    }
  }
};
