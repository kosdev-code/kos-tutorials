import { css, Global } from '@emotion/react';
import styled from '@emotion/styled';
import {
  kosComponent,
  KosLog,
  Trans,
  useKosTranslation,
} from '@kosdev-code/kos-ui-sdk';

const log = KosLog.createLogger({ name: 'main-view' });
log.debug('main-view component loaded');

export const MainView: React.FunctionComponent = kosComponent(() => {
  const { t } = useKosTranslation('theme-sample');
  return (
    <>
      <Global
        styles={css`
          .kos-theme-dark {
            --kos-component-logo-url: url('./assets/kos-logo.svg');
          }

          .kos-theme-light {
            --kos-component-logo-url: url('./assets/kos-logo-light.svg');
          }
        `}
      />
      <Main>
        <div className="logo"></div>
        <h1>{t('welcome', { default: 'You are now connected.' })}</h1>
        <h2>
          <Trans
            t={t}
            i18nKey="visit"
            defaults={
              'For more information, visit <link>www.kosdev.com</link>.'
            }
            components={{ link: <a href="http://www.kosdev.com" /> }}
          />
        </h2>

        <div className="footer"></div>
      </Main>
    </>
  );
});

const Main = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
  text-align: center;
  padding-top: 180px;
  position: relative;

  .logo {
    display: flex;
    justify-content: center;
    align-items: center;
    width: 200px;
    height: 72px;

    background-size: cover;
    background-image: var(--kos-component-logo-url);
  }

  .footer {
    position: fixed;
    bottom: 0;
    left: 0;
    width: 100%;
    height: 50vh;
    background-image: url('./assets/graphics.png');
    background-size: cover;
  }
`;
