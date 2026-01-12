import styled from '@emotion/styled';
import { Button } from '../button';

const Icon = styled.img`
  width: 27%;
`;

export const DecreaseButton = () => (
  <Button>
    <Icon alt="Decrease Icon" src="/assets/icons/decrease-icon.svg" />
  </Button>
);
