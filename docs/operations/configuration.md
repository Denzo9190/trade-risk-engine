# Configuration

Risk engine parameters are configured via `application.yml`.

## Risk Limits

risk:
max-trade-size: 5
max-position-size: 10
max-portfolio-exposure: 500000
max-price-deviation: 0.01

### max-trade-size
Maximum allowed trade quantity.

### max-position-size
Maximum allowed absolute position size per symbol.

### max-portfolio-exposure
Maximum allowed portfolio exposure.

### max-price-deviation
Maximum allowed deviation between signal price and market price.

Example:

signal = 61000  
market = 60000

deviation = 1.6667% → trade rejected