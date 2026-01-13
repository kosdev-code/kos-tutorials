import styled from '@emotion/styled';
import { Button, ButtonProps } from '../button';

const Icon = styled.img`
  width: 27%;
`;

export const IncreaseButton = (props: Omit<ButtonProps, 'children'>) => (
  <Button {...props}>
    <Icon alt="Increase Icon" src="/assets/icons/increase-icon.svg" />
  </Button>
);
