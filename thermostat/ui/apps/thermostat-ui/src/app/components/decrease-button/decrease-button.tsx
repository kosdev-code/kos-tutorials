import styled from '@emotion/styled';
import { Button, ButtonProps } from '../button';

const Icon = styled.img`
  width: 27%;
`;

export const DecreaseButton = (props: Omit<ButtonProps, 'children'>) => (
  <Button {...props}>
    <Icon alt="Decrease Icon" src="./assets/icons/decrease-icon.svg" />
  </Button>
);
