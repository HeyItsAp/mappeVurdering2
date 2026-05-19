package ntnu.gruppe21;

import ntnu.gruppe21.gameEngine.strategies.marketsimulator.MarketSimulator;
import ntnu.gruppe21.gameEngine.strategies.marketsimulator.MarketSimStock;

import java.awt.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

class TestRun {

  static void run() {
    final int TICKS = 50;

    MarketSimulator sim = new MarketSimulator();
    MarketSimStock stock = new MarketSimStock("SIM", "Simulator Co.", BigDecimal.valueOf(500.0));

    List<Double> prices = new ArrayList<>();
    prices.add(stock.getSalesPrice().doubleValue());
    for (int t = 0; t < TICKS; t++) {
      sim.calculateNewPrice(List.of(stock));
      prices.add(stock.getSalesPrice().doubleValue());
      System.out.println(stock.getD() + " " + t);
    }

    SwingUtilities.invokeLater(() -> showGraph(prices, TICKS));
  }

  private static void showGraph(List<Double> prices, int ticks) {
    JFrame frame = new JFrame("MarketSimulator — " + ticks + " ticks");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(900, 500);
    frame.setLocationRelativeTo(null);

    JPanel panel =
        new JPanel() {
          @Override
          protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int padLeft = 60, padRight = 20, padTop = 40, padBottom = 40;
            int w = getWidth() - padLeft - padRight;
            int h = getHeight() - padTop - padBottom;

            double min = prices.stream().mapToDouble(Double::doubleValue).min().orElse(0);
            double max = prices.stream().mapToDouble(Double::doubleValue).max().orElse(1);
            double range = max == min ? 1 : max - min;

            // Chart background
            g2.setColor(Color.WHITE);
            g2.fillRect(padLeft, padTop, w, h);

            // Horizontal grid lines + Y-axis labels
            Font labelFont = new Font("SansSerif", Font.PLAIN, 11);
            g2.setFont(labelFont);
            FontMetrics fm = g2.getFontMetrics();
            int yDivisions = 6;
            for (int i = 0; i <= yDivisions; i++) {
              int y = padTop + h - (int) ((double) i / yDivisions * h);
              g2.setColor(new Color(230, 230, 230));
              g2.drawLine(padLeft, y, padLeft + w, y);
              double labelVal = min + ((double) i / yDivisions) * range;
              String text = String.format("%.1f", labelVal);
              g2.setColor(Color.DARK_GRAY);
              g2.drawString(text, padLeft - fm.stringWidth(text) - 6, y + fm.getAscent() / 2);
            }

            // Chart border
            g2.setColor(new Color(180, 180, 180));
            g2.drawRect(padLeft, padTop, w, h);

            // Price line
            g2.setColor(new Color(30, 100, 215));
            g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            int n = prices.size();
            for (int i = 1; i < n; i++) {
              int x1 = padLeft + (int) ((double) (i - 1) / (n - 1) * w);
              int y1 = padTop + h - (int) ((prices.get(i - 1) - min) / range * h);
              int x2 = padLeft + (int) ((double) i / (n - 1) * w);
              int y2 = padTop + h - (int) ((prices.get(i) - min) / range * h);
              g2.drawLine(x1, y1, x2, y2);
            }

            // X-axis calculateNewPrice labels
            g2.setStroke(new BasicStroke(1f));
            g2.setColor(Color.DARK_GRAY);
            int xDivisions = 10;
            for (int i = 0; i <= xDivisions; i++) {
              int x = padLeft + (int) ((double) i / xDivisions * w);
              int tickLabel = (int) ((double) i / xDivisions * ticks);
              g2.drawLine(x, padTop + h, x, padTop + h + 4);
              String text = String.valueOf(tickLabel);
              g2.drawString(text, x - fm.stringWidth(text) / 2, padTop + h + 4 + fm.getAscent());
            }

            // Title
            g2.setFont(new Font("SansSerif", Font.BOLD, 13));
            g2.setColor(Color.BLACK);
            g2.drawString("MarketSimulator — " + ticks + " ticks", padLeft, padTop - 12);
          }
        };

    panel.setBackground(new Color(245, 245, 245));
    frame.add(panel);
    frame.setVisible(true);
  }
}
