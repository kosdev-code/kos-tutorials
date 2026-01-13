import styled from '@emotion/styled';
import { ReactNode } from 'react';
import { BorderBaseContainer } from '../../styles';

const Container = styled(BorderBaseContainer)`
  cursor: pointer;
  height: 73px;
  width: 176px;
`;

export interface ButtonProps {
  children: ReactNode;
  onPointerDown?: () => void;
}

export const Button = ({ children, onPointerDown }: ButtonProps) => {
  return <Container onPointerDown={onPointerDown}>{children}</Container>;
};
