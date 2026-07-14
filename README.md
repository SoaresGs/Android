# Gestão Financeira

Aplicativo Android nativo (Java) para controle de gastos pessoais mensais.

## Funcionalidades

- **Painel mensal (dashboard)**: total gasto no mês e navegação entre meses
  (◀ / ▶) para consultar meses anteriores e futuros.
- **Lançamento de gastos** por categoria:
  - **Fixo** — despesas mensais fixas (aluguel, assinaturas, contas)
  - **Variável** — despesas que mudam de mês a mês
  - **Mercado** — compras de supermercado
  - **Fast Food** — lanches e delivery
  - **Parcela** — compras parceladas, com controle de parcela atual/total e
    das **parcelas restantes**
  - **Outros** — demais gastos
- **Resumo por categoria**: cartões com o total de cada categoria no mês.
- **Controle de parcelas**: o painel mostra quantas parcelas ainda faltam e o
  valor total a pagar.
- **Lista de gastos do mês**: toque para **editar** e toque longo para **excluir**.
- **Renda mensal e saldo**: informe sua renda do mês e veja quanto sobra
  (o saldo fica vermelho quando negativo).
- **Orçamento por categoria**: defina um limite mensal para cada categoria; o
  painel mostra uma barra de progresso e alerta **"Estourou!"** ao ultrapassar.
- **Gráfico de distribuição**: gráfico de rosca (pizza) com a proporção dos
  gastos por categoria, desenhado nativamente (sem bibliotecas externas).
- **Repetir gastos fixos**: copia as despesas fixas do mês anterior para o mês
  atual com um toque, sem duplicar.
- **Exportar**: gera um arquivo CSV (abre no Excel/Google Planilhas) e permite
  compartilhar por qualquer app.

Os dados ficam salvos localmente no aparelho (SQLite), sem necessidade de
internet ou login.

## Estrutura do projeto

```
app/src/main/java/com/example/gestaofinanceira/
├── Activity/
│   ├── MainActivity.java             # painel/dashboard
│   ├── AdicionarGastoActivity.java   # cadastro e edição de gasto
│   ├── ListarGastosActivity.java     # lista de gastos do mês
│   └── OrcamentoActivity.java        # limites por categoria
├── Adapter/AdapterGasto.java         # item da lista
├── DAO/
│   ├── GastoDAO.java                 # gastos, agregações e repetir fixos
│   ├── RendaDAO.java                 # renda mensal
│   └── OrcamentoDAO.java             # orçamento por categoria
├── DBhelper/
│   ├── DBhelper.java                 # criação do banco SQLite
│   └── RecyclerItemClickListener.java
├── Model/Gasto.java                  # modelo de gasto
├── View/GraficoPizzaView.java        # gráfico de rosca (Canvas)
└── Util/
    ├── MoedaUtil.java                # formatação de moeda e meses
    └── Exportador.java               # exportação CSV
```

## Como compilar

Requisitos: Android Studio (ou SDK do Android) com API 29.

```bash
./gradlew assembleDebug
```

O APK é gerado em `app/build/outputs/apk/debug/`.

- `minSdkVersion`: 21 (Android 5.0)
- `targetSdkVersion` / `compileSdkVersion`: 29
