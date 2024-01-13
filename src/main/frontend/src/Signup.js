import React, { useState } from 'react';
import axios from 'axios';

const Signup = () => {
  const [formData, setFormData] = useState({
    name: '',
    password: '',
    passwordCheck: '',
    nickname: '',
    email: '',
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prevData) => ({
      ...prevData,
      [name]: value,
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      // 서버로의 POST 요청 보내기
      const response = await axios.post('/api/members/signup', formData);

      // 성공적으로 회원가입되면, 서버의 응답(response)을 출력
      console.log('회원가입 성공:', response.data);
    } catch (error) {
      // 에러가 발생하면 콘솔에 에러 출력
      console.error('회원가입 에러:', error);
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <label>
        아이디:
        <input
          type="text"
          name="name"
          value={formData.name}
          onChange={handleChange}
        />
      </label>
      <br />

      <label>
        비밀번호:
        <input
          type="password"
          name="password"
          value={formData.password}
          onChange={handleChange}
        />
      </label>
      <br />

      <label>
        비밀번호확인:
        <input
          type="password"
          name="passwordCheck"
          value={formData.passwordCheck}
          onChange={handleChange}
        />
      </label>
      <br />

      <label>
        닉네임:
        <input
          type="text"
          name="nickname"
          value={formData.nickname}
          onChange={handleChange}
        />
      </label>
      <br />

      <label>
        이메일:
        <input
          type="email"
          name="email"
          value={formData.email}
          onChange={handleChange}
        />
      </label>
      <br />

      <button type="submit">회원가입</button>
    </form>
  );
};

export default Signup;
