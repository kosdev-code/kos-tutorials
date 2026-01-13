import styled from '@emotion/styled';
import { BaseContainer } from '../../styles';

export const TemperatureContainer = styled(BaseContainer)`
  color: #191919;
  font-family: Montserrat;
  font-size: 40px;
  font-style: normal;
  font-weight: 600;
  height: 136px;
  line-height: 120%; /* 48px */
  text-align: center;
  width: 176px;
`;

export const CoolingContainer = styled(TemperatureContainer)`
  background-color: #83c9f4;
`;

export const HeatingContainer = styled(TemperatureContainer)`
  background-color: #ffa69e;
`;
