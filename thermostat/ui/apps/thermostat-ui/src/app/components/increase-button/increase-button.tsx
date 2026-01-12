import styled from '@emotion/styled';
import { Button } from '../button';

const Icon = styled.img`
  width: 27%;
`;

export const IncreaseButton = () => (
  <Button>
    <Icon alt="Increase Icon" src="/assets/icons/increase-icon.svg" />
  </Button>
);
