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
- **Lista de gastos do mês**: com exclusão por toque longo no item.

Os dados ficam salvos localmente no aparelho (SQLite), sem necessidade de
internet ou login.

## Estrutura do projeto

```
app/src/main/java/com/example/gestaofinanceira/
├── Activity/
│   ├── MainActivity.java            # painel/dashboard
│   ├── AdicionarGastoActivity.java  # cadastro de gasto
│   └── ListarGastosActivity.java    # lista de gastos do mês
├── Adapter/AdapterGasto.java        # item da lista
├── DAO/GastoDAO.java                # acesso a dados e agregações
├── DBhelper/
│   ├── DBhelper.java                # criação do banco SQLite
│   └── RecyclerItemClickListener.java
├── Model/Gasto.java                 # modelo de gasto
└── Util/MoedaUtil.java              # formatação de moeda e meses
```

## Como compilar

Requisitos: Android Studio (ou SDK do Android) com API 29.

```bash
./gradlew assembleDebug
```

O APK é gerado em `app/build/outputs/apk/debug/`.

- `minSdkVersion`: 21 (Android 5.0)
- `targetSdkVersion` / `compileSdkVersion`: 29
