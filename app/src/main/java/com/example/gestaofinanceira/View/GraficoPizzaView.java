package com.example.gestaofinanceira.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Gráfico de pizza simples desenhado no Canvas, sem dependências externas.
 * Recebe fatias com rótulo, valor e cor.
 */
public class GraficoPizzaView extends View {

    public static class Fatia {
        final String rotulo;
        final double valor;
        final int cor;

        public Fatia(String rotulo, double valor, int cor) {
            this.rotulo = rotulo;
            this.valor = valor;
            this.cor = cor;
        }
    }

    private final List<Fatia> fatias = new ArrayList<>();
    private double total = 0;

    private final Paint paintFatia = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint paintFuro = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint paintTexto = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF area = new RectF();

    public GraficoPizzaView(Context context) {
        super(context);
        init();
    }

    public GraficoPizzaView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paintFuro.setColor(Color.WHITE);
        paintTexto.setColor(Color.parseColor("#757575"));
        paintTexto.setTextSize(36f);
        paintTexto.setTextAlign(Paint.Align.CENTER);
    }

    /** Define as fatias a exibir e redesenha. */
    public void setFatias(List<Fatia> novas) {
        fatias.clear();
        total = 0;
        if (novas != null) {
            for (Fatia f : novas) {
                if (f.valor > 0) {
                    fatias.add(f);
                    total += f.valor;
                }
            }
        }
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int largura = MeasureSpec.getSize(widthMeasureSpec);
        int altura = (int) (200 * getResources().getDisplayMetrics().density);
        setMeasuredDimension(largura, altura);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int largura = getWidth();
        int altura = getHeight();
        float diametro = Math.min(largura, altura) - 24f;
        float esquerda = (largura - diametro) / 2f;
        float topo = (altura - diametro) / 2f;
        area.set(esquerda, topo, esquerda + diametro, topo + diametro);

        if (total <= 0) {
            paintTexto.setTextSize(38f);
            canvas.drawText("Sem gastos neste mês", largura / 2f, altura / 2f, paintTexto);
            return;
        }

        float inicio = -90f;
        for (Fatia f : fatias) {
            float varredura = (float) (f.valor / total * 360.0);
            paintFatia.setColor(f.cor);
            canvas.drawArc(area, inicio, varredura, true, paintFatia);
            inicio += varredura;
        }

        // Furo central para efeito "rosca".
        float raioFuro = diametro * 0.28f;
        canvas.drawCircle(area.centerX(), area.centerY(), raioFuro, paintFuro);

        paintTexto.setTextSize(30f);
        canvas.drawText("Gastos", area.centerX(), area.centerY() + 10f, paintTexto);
    }
}
