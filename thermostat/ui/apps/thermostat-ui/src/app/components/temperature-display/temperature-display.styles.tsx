import styled from '@emotion/styled';

export const Container = styled.div`
  align-items: center;
  display: flex;
  flex-direction: column;
  gap: 4px;
  height: 290px;
  justify-content: center;
  user-select: none;
  width: 381px;
`;

export const IconContainer = styled.div`
  align-items: center;
  display: flex;
  height: 48px;
  justify-content: center;
`;

export const Icon = styled.img`
  width: 10%;
`;

export const Temperature = styled.div`
  color: #191919;
  font-family: Montserrat;
  font-size: 56px;
  font-style: normal;
  font-weight: 600;
  line-height: 120%; /* 67.2px */
  text-align: center;
`;

export const Mode = styled.div`
  color: #afb2b4;
  font-family: Montserrat;
  font-size: 24px;
  font-style: normal;
  font-weight: 700;
  line-height: 120%; /* 28.8px */
  text-align: center;
`;

export const CoolingMode = styled(Mode)`
  color: #83c9f4;
`;

export const HeatingMode = styled(Mode)`
  color: #ffa69e;
`;
