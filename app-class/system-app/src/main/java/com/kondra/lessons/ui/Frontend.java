package com.kondra.lessons.ui;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.function.Supplier;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import com.kondra.device.mgmt.data.DeviceManagementInfo;

public class Frontend extends JFrame {

    // -------------------------------------------------------------------------
    // Palette
    // -------------------------------------------------------------------------
    private static final Color BG_PRIMARY = new Color(0x0F, 0x17, 0x23);
    private static final Color BG_CARD = new Color(0x16, 0x21, 0x30);
    private static final Color ACCENT_BLUE = new Color(0x00, 0x9F, 0xFF);
    private static final Color ACCENT_CYAN = new Color(0x00, 0xD4, 0xC8);
    private static final Color ACCENT_ORANGE = new Color(0xFF, 0x8C, 0x42);
    private static final Color ACCENT_GREEN = new Color(0x00, 0xE5, 0x7F);
    private static final Color TEXT_PRIMARY = new Color(0xE8, 0xEF, 0xFF);
    private static final Color TEXT_MUTED = new Color(0x5A, 0x72, 0x94);
    private static final Color TEXT_LABEL = new Color(0x8A, 0xA3, 0xC8);
    private static final Color BORDER_DIM = new Color(0x1E, 0x2E, 0x44);
    private static final Color PULSE_ON = new Color(0x00, 0xE5, 0x7F);
    private static final Color PULSE_OFF = new Color(0x1E, 0x2E, 0x44);

    // -------------------------------------------------------------------------
    // Fonts — derived from the runtime system font so no external typeface needed
    // -------------------------------------------------------------------------
    private static final Font SYS;
    static {
        SYS = new JLabel().getFont().deriveFont(Font.PLAIN, 12f);
    }
    private static final Font FONT_TITLE = SYS.deriveFont(Font.BOLD, 20f);
    private static final Font FONT_LABEL = SYS.deriveFont(Font.PLAIN, 10f);
    private static final Font FONT_VALUE = SYS.deriveFont(Font.BOLD, 14f);
    private static final Font FONT_STATUS = SYS.deriveFont(Font.BOLD, 11f);
    private static final Font FONT_FOOTER = SYS.deriveFont(Font.PLAIN, 10f);

    // -------------------------------------------------------------------------
    // Live UI nodes
    // -------------------------------------------------------------------------
    private JLabel ownerValue;
    private JLabel countryValue;
    private JLabel addressValue;
    private PulseIndicator pulseIndicator;
    private JLabel heartbeatLabel;
    private JLabel statusBadge;
    private SnackbarOverlay snackbar;

    // -------------------------------------------------------------------------
    // Callbacks — set by FrontendController to keep business logic out of the view
    // -------------------------------------------------------------------------
    private Supplier<String> onGetApiKey;

    // =========================================================================
    // Constructor — initial "waiting for data" state
    // =========================================================================
    public Frontend() {
        super("Device Management Console");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(720, 460);
        setMinimumSize(new Dimension(640, 400));
        setLocationRelativeTo(null);
        setBackground(BG_PRIMARY);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_PRIMARY);

        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildCenter(), BorderLayout.CENTER);
        root.add(buildFooter(), BorderLayout.SOUTH);

        // Layered pane lets the snackbar float above everything
        JLayeredPane layered = new JLayeredPane();
        layered.setLayout(new OverlayLayout(layered));
        layered.add(root, JLayeredPane.DEFAULT_LAYER);

        snackbar = new SnackbarOverlay();
        layered.add(snackbar, JLayeredPane.POPUP_LAYER);

        setContentPane(layered);
        setVisible(true);
    }

    // =========================================================================
    // Public API — called by FrontendController
    // =========================================================================

    public void setDeviceManagementInfo(DeviceManagementInfo info) {
        ownerValue.setText(nvl(info.getOwner(), "—"));
        countryValue.setText(nvl(info.getCountry(), "—"));
        addressValue.setText(nvl(info.getPhysicalAddress(), "—"));

        statusBadge.setText("  ONLINE  ");
        statusBadge.setForeground(ACCENT_GREEN);
        statusBadge.setBackground(new Color(0x00, 0x2B, 0x1A));
        statusBadge.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0x00, 0x50, 0x30), 1),
                new EmptyBorder(4, 8, 4, 8)));
        repaint();
    }

    public void showHeartbeat() {
        pulseIndicator.pulse();
        heartbeatLabel.setText("Last heartbeat: just now");
        snackbar.show("Heartbeat received", ACCENT_GREEN);
    }

    public void setOnGetApiKey(Supplier<String> onGetApiKey) {
        this.onGetApiKey = onGetApiKey;
    }

    private void onApiKeyButtonClicked() {
        if (onGetApiKey == null) return;
        String key = onGetApiKey.get();
        SwingUtilities.invokeLater(() -> new ApiKeyDialog(this, key).setVisible(true));
    }

    // =========================================================================
    // UI builders
    // =========================================================================

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG_CARD);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_DIM),
                new EmptyBorder(14, 22, 14, 22)));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        left.setOpaque(false);
        left.add(new JLabel(deviceIcon()));

        JPanel titleBlock = new JPanel(new GridLayout(2, 1, 0, 2));
        titleBlock.setOpaque(false);
        titleBlock.add(styledLabel("Device Management", FONT_TITLE, TEXT_PRIMARY));
        titleBlock.add(styledLabel("System Monitoring Console", FONT_LABEL, TEXT_MUTED));
        left.add(titleBlock);

        statusBadge = new JLabel("  WAITING  ", SwingConstants.CENTER);
        statusBadge.setFont(FONT_STATUS);
        statusBadge.setForeground(new Color(0xB0, 0x80, 0x20));
        statusBadge.setBackground(new Color(0x2B, 0x1E, 0x00));
        statusBadge.setOpaque(true);
        statusBadge.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0x4A, 0x38, 0x00), 1),
                new EmptyBorder(4, 8, 4, 8)));

        StyledButton apiKeyButton = new StyledButton("Get API Key", ACCENT_BLUE);
        apiKeyButton.addActionListener(e -> onApiKeyButtonClicked());

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);
        right.add(apiKeyButton);
        right.add(statusBadge);

        header.add(left, BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);
        return header;
    }

    private JPanel buildCenter() {
        JPanel center = new JPanel(new BorderLayout(0, 0));
        center.setBackground(BG_PRIMARY);
        center.setBorder(new EmptyBorder(26, 26, 0, 26));

        JLabel sectionLabel = styledLabel("DEVICE INFORMATION", FONT_LABEL, TEXT_MUTED);
        sectionLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
        center.add(sectionLabel, BorderLayout.NORTH);

        JPanel cards = new JPanel(new GridLayout(1, 3, 14, 0));
        cards.setOpaque(false);

        ownerValue = valuePlaceholder();
        countryValue = valuePlaceholder();
        addressValue = valuePlaceholder();

        cards.add(infoCard("Owner", ownerValue, ownerIcon(), ACCENT_BLUE));
        cards.add(infoCard("Country", countryValue, globeIcon(), ACCENT_CYAN));
        cards.add(infoCard("Physical Address", addressValue, pinIcon(), ACCENT_ORANGE));

        center.add(cards, BorderLayout.CENTER);
        return center;
    }

    private JPanel buildFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(BG_CARD);
        footer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_DIM),
                new EmptyBorder(10, 22, 10, 22)));

        pulseIndicator = new PulseIndicator();
        heartbeatLabel = styledLabel("Waiting for heartbeat…", FONT_FOOTER, TEXT_MUTED);

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        left.setOpaque(false);
        left.add(pulseIndicator);
        left.add(heartbeatLabel);

        footer.add(left, BorderLayout.WEST);
        footer.add(styledLabel("v1.0  •  KOS Device Management", FONT_FOOTER, TEXT_MUTED),
                BorderLayout.EAST);
        return footer;
    }

    private JPanel infoCard(String labelText, JLabel valueLabel, Icon icon, Color accent) {
        RoundedPanel card = new RoundedPanel(14, BG_CARD);
        card.setLayout(new BorderLayout());

        // Accent top stripe
        JPanel stripe = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, accent, getWidth(), 0, accent.darker().darker()));
                g2.fillRoundRect(0, 0, getWidth(), 4, 4, 4);
                g2.dispose();
            }
        };
        stripe.setPreferredSize(new Dimension(0, 4));
        stripe.setOpaque(false);

        JPanel body = new JPanel(new GridBagLayout());
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(14, 16, 14, 16));

        GridBagConstraints gc = new GridBagConstraints();

        gc.gridx = 0;
        gc.gridy = 0;
        gc.gridheight = 2;
        gc.insets = new Insets(0, 0, 0, 12);
        gc.anchor = GridBagConstraints.NORTHWEST;
        body.add(new JLabel(icon), gc);

        gc.gridx = 1;
        gc.gridy = 0;
        gc.gridheight = 1;
        gc.insets = new Insets(0, 0, 4, 0);
        gc.weightx = 1;
        gc.fill = GridBagConstraints.HORIZONTAL;
        body.add(styledLabel(labelText.toUpperCase(), FONT_LABEL, TEXT_LABEL), gc);

        gc.gridy = 1;
        gc.insets = new Insets(0, 0, 0, 0);
        body.add(valueLabel, gc);

        card.add(stripe, BorderLayout.NORTH);
        card.add(body, BorderLayout.CENTER);
        return card;
    }

    // =========================================================================
    // Helpers
    // =========================================================================

    private static JLabel styledLabel(String text, Font font, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(font);
        l.setForeground(color);
        return l;
    }

    private static JLabel valuePlaceholder() {
        JLabel l = new JLabel("Loading…");
        l.setFont(FONT_VALUE);
        l.setForeground(TEXT_MUTED);
        return l;
    }

    private static String nvl(String s, String fallback) {
        return (s == null || s.isBlank()) ? fallback : s;
    }

    // =========================================================================
    // Vector icons
    // =========================================================================

    private static Icon deviceIcon() {
        return sizedIcon(36, 36, (c, g, x, y) -> {
            g.setColor(new Color(0x00, 0x40, 0x80, 160));
            g.fillRoundRect(x, y, 36, 36, 10, 10);
            g.setColor(ACCENT_BLUE);
            g.setStroke(new BasicStroke(2f));
            g.drawRoundRect(x + 4, y + 6, 28, 20, 4, 4);
            g.setStroke(new BasicStroke(2.5f));
            g.drawLine(x + 18, y + 26, x + 18, y + 30);
            g.drawLine(x + 12, y + 30, x + 24, y + 30);
        });
    }

    // Icons implement Icon via anonymous classes (lambda is not allowed for Icon
    // because it has 3 abstract methods). Keeping them concise with a helper.

    private static Icon ownerIcon() {
        return sizedIcon(28, 28, (c, g, x, y) -> {
            g.setColor(new Color(ACCENT_BLUE.getRed(), ACCENT_BLUE.getGreen(), ACCENT_BLUE.getBlue(), 30));
            g.fillOval(x, y, 28, 28);
            g.setColor(ACCENT_BLUE);
            g.setStroke(new BasicStroke(1.8f));
            g.drawOval(x + 8, y + 4, 12, 12);
            g.drawArc(x + 2, y + 16, 24, 14, 0, 180);
        });
    }

    private static Icon globeIcon() {
        return sizedIcon(28, 28, (c, g, x, y) -> {
            g.setColor(new Color(ACCENT_CYAN.getRed(), ACCENT_CYAN.getGreen(), ACCENT_CYAN.getBlue(), 30));
            g.fillOval(x, y, 28, 28);
            g.setColor(ACCENT_CYAN);
            g.setStroke(new BasicStroke(1.8f));
            g.drawOval(x + 2, y + 2, 24, 24);
            g.drawOval(x + 8, y + 2, 12, 24);
            g.drawLine(x + 2, y + 14, x + 26, y + 14);
            g.drawLine(x + 14, y + 2, x + 14, y + 26);
        });
    }

    private static Icon pinIcon() {
        return sizedIcon(28, 28, (c, g, x, y) -> {
            g.setColor(new Color(ACCENT_ORANGE.getRed(), ACCENT_ORANGE.getGreen(), ACCENT_ORANGE.getBlue(), 30));
            g.fillOval(x, y, 28, 28);
            g.setColor(ACCENT_ORANGE);
            g.setStroke(new BasicStroke(1.8f));
            g.drawOval(x + 9, y + 4, 10, 10);
            g.drawLine(x + 14, y + 14, x + 14, y + 25);
        });
    }

    @FunctionalInterface
    private interface IconPainter {
        void paint(Component c, Graphics2D g, int x, int y);
    }

    private static Icon sizedIcon(int w, int h, IconPainter painter) {
        return new Icon() {
            public int getIconWidth() {
                return w;
            }

            public int getIconHeight() {
                return h;
            }

            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                painter.paint(c, g2, x, y);
                g2.dispose();
            }
        };
    }

    // =========================================================================
    // Inner: rounded card panel
    // =========================================================================
    private static class RoundedPanel extends JPanel {
        private final int radius;
        private final Color bg;

        RoundedPanel(int radius, Color bg) {
            this.radius = radius;
            this.bg = bg;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bg);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), radius, radius));
            g2.setColor(BORDER_DIM);
            g2.setStroke(new BasicStroke(1f));
            g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1, getHeight() - 1, radius, radius));
            g2.dispose();
        }
    }

    // =========================================================================
    // Inner: animated pulse dot
    // =========================================================================
    private static class PulseIndicator extends JPanel {
        private Color dotColor = PULSE_OFF;
        private float alpha = 0f;
        private Timer fadeTimer;

        PulseIndicator() {
            setPreferredSize(new Dimension(14, 14));
            setOpaque(false);
        }

        void pulse() {
            if (fadeTimer != null && fadeTimer.isRunning())
                fadeTimer.stop();
            dotColor = PULSE_ON;
            alpha = 1f;
            repaint();
            fadeTimer = new Timer(40, null);
            fadeTimer.addActionListener((ActionEvent e) -> {
                alpha -= 0.04f;
                if (alpha <= 0f) {
                    alpha = 0f;
                    dotColor = PULSE_OFF;
                    fadeTimer.stop();
                }
                repaint();
            });
            fadeTimer.start();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (alpha > 0f) {
                g2.setColor(new Color(PULSE_ON.getRed(), PULSE_ON.getGreen(), PULSE_ON.getBlue(),
                        (int) (alpha * 60)));
                g2.fillOval(0, 0, 14, 14);
            }
            g2.setColor(dotColor);
            g2.fillOval(3, 3, 8, 8);
            g2.dispose();
        }
    }

    // =========================================================================
    // Inner: styled button
    // =========================================================================
    private static class StyledButton extends JButton {
        private final Color accent;
        private boolean hovered = false;

        StyledButton(String text, Color accent) {
            super(text);
            this.accent = accent;
            setFont(FONT_STATUS);
            setForeground(accent);
            setOpaque(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(6, 14, 6, 14));

            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
                @Override public void mouseExited(MouseEvent e)  { hovered = false; repaint(); }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Color bg = hovered
                ? new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 40)
                : new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 18);
            g2.setColor(bg);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);

            g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), hovered ? 200 : 120));
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);

            g2.dispose();
            super.paintComponent(g);
        }
    }

    // =========================================================================
    // Inner: API key dialog
    // =========================================================================
    private static class ApiKeyDialog extends JDialog {
        ApiKeyDialog(JFrame parent, String apiKey) {
            super(parent, "API Key", true);
            setUndecorated(true);
            setBackground(new Color(0, 0, 0, 0));

            JPanel root = new JPanel(new BorderLayout(0, 0)) {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    // shadow
                    g2.setColor(new Color(0, 0, 0, 60));
                    g2.fillRoundRect(4, 6, getWidth() - 4, getHeight() - 6, 16, 16);
                    // body
                    g2.setColor(new Color(0x16, 0x21, 0x30));
                    g2.fillRoundRect(0, 0, getWidth() - 4, getHeight() - 6, 16, 16);
                    // border
                    g2.setColor(BORDER_DIM);
                    g2.setStroke(new BasicStroke(1f));
                    g2.drawRoundRect(0, 0, getWidth() - 5, getHeight() - 7, 16, 16);
                    g2.dispose();
                }
            };
            root.setOpaque(false);
            root.setBorder(new EmptyBorder(24, 28, 20, 28));

            // Title row
            JPanel titleRow = new JPanel(new BorderLayout());
            titleRow.setOpaque(false);
            titleRow.setBorder(new EmptyBorder(0, 0, 16, 0));

            JLabel title = styledLabel("API Key", FONT_VALUE, TEXT_PRIMARY);
            JLabel sub   = styledLabel("Keep this key secure — do not share it publicly.",
                                       FONT_LABEL, TEXT_MUTED);
            JPanel titleBlock = new JPanel(new GridLayout(2, 1, 0, 3));
            titleBlock.setOpaque(false);
            titleBlock.add(title);
            titleBlock.add(sub);
            titleRow.add(titleBlock, BorderLayout.CENTER);

            // Close button
            JButton closeBtn = new JButton("✕");
            closeBtn.setFont(SYS.deriveFont(Font.BOLD, 13f));
            closeBtn.setForeground(TEXT_MUTED);
            closeBtn.setOpaque(false);
            closeBtn.setContentAreaFilled(false);
            closeBtn.setBorderPainted(false);
            closeBtn.setFocusPainted(false);
            closeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            closeBtn.addActionListener(e -> dispose());
            closeBtn.addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { closeBtn.setForeground(TEXT_PRIMARY); }
                @Override public void mouseExited(MouseEvent e)  { closeBtn.setForeground(TEXT_MUTED);   }
            });
            titleRow.add(closeBtn, BorderLayout.EAST);

            // Key display box
            JPanel keyBox = new JPanel(new BorderLayout(10, 0)) {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(BG_PRIMARY);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    g2.setColor(new Color(ACCENT_BLUE.getRed(), ACCENT_BLUE.getGreen(), ACCENT_BLUE.getBlue(), 80));
                    g2.setStroke(new BasicStroke(1f));
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                    g2.dispose();
                }
            };
            keyBox.setOpaque(false);
            keyBox.setBorder(new EmptyBorder(10, 14, 10, 10));

            JLabel keyLabel = new JLabel(apiKey != null ? apiKey : "—");
            keyLabel.setFont(SYS.deriveFont(Font.PLAIN, 12f));
            keyLabel.setForeground(ACCENT_CYAN);

            StyledButton copyBtn = new StyledButton("Copy", ACCENT_BLUE);
            copyBtn.addActionListener(e -> {
                if (apiKey != null) {
                    Toolkit.getDefaultToolkit().getSystemClipboard()
                        .setContents(new StringSelection(apiKey), null);
                    copyBtn.setText("Copied!");
                    new Timer(1500, ev -> { copyBtn.setText("Copy"); ((Timer) ev.getSource()).stop(); }).start();
                }
            });

            keyBox.add(keyLabel, BorderLayout.CENTER);
            keyBox.add(copyBtn,  BorderLayout.EAST);

            root.add(titleRow, BorderLayout.NORTH);
            root.add(keyBox,   BorderLayout.CENTER);

            setContentPane(root);
            pack();
            setSize(Math.max(getWidth(), 460), getHeight());
            setLocationRelativeTo(parent);
        }
    }

    // =========================================================================
    // Inner: floating snackbar toast
    // =========================================================================
    private static class SnackbarOverlay extends JPanel {
        private String message = "";
        private Color accentColor = ACCENT_GREEN;
        private float opacity = 0f;
        private boolean showing = false;
        private Timer hideTimer;

        SnackbarOverlay() {
            setOpaque(false);
        }

        void show(String msg, Color accent) {
            this.message = msg;
            this.accentColor = accent;
            this.opacity = 1f;
            this.showing = true;
            repaint();
            if (hideTimer != null && hideTimer.isRunning())
                hideTimer.stop();
            hideTimer = new Timer(1000, null);
            hideTimer.setRepeats(false);
            hideTimer.addActionListener(e -> fadeOut());
            hideTimer.start();
        }

        private void fadeOut() {
            Timer ft = new Timer(50, null);
            ft.addActionListener((ActionEvent e) -> {
                opacity -= 0.08f;
                if (opacity <= 0f) {
                    opacity = 0f;
                    showing = false;
                    ft.stop();
                }
                repaint();
            });
            ft.start();
        }

        @Override
        protected void paintComponent(Graphics g) {
            if (!showing || opacity <= 0f)
                return;
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));

            int w = 260, h = 44;
            int x = (getWidth() - w) / 2;
            int y = getHeight() - h - 24;

            g2.setColor(new Color(0, 0, 0, 80));
            g2.fillRoundRect(x + 2, y + 4, w, h, 12, 12);

            g2.setColor(new Color(0x12, 0x1E, 0x2E));
            g2.fillRoundRect(x, y, w, h, 12, 12);

            g2.setColor(accentColor);
            g2.fillRoundRect(x, y, 4, h, 4, 4);
            g2.fillRect(x + 2, y, 4, h);

            g2.setColor(new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 80));
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(x, y, w, h, 12, 12);

            Font snackFont = SYS.deriveFont(Font.BOLD, 12f);
            g2.setFont(snackFont);
            g2.setColor(TEXT_PRIMARY);
            FontMetrics fm = g2.getFontMetrics();
            int tx = x + (w - fm.stringWidth(message)) / 2 + 4;
            int ty = y + (h - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(message, tx, ty);

            g2.dispose();
        }
    }
}
