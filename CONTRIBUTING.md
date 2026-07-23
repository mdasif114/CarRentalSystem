# Contributing

This repository is intentionally small and domain-focused. Contributions should improve correctness, clarity, testability, or documented engineering trade-offs.

## Development expectations

- Use Java 21 language and library features only when they improve the design.
- Keep domain rules in the service/model layers rather than controllers.
- Add or update tests with every behavior change.
- Keep API changes documented in the README.
- Do not add credentials, customer data, proprietary material, or environment-specific infrastructure.
- Separate implemented behavior from future design ideas.

## Before opening a pull request

~~~bash
./mvnw verify
~~~

A pull request should explain what changed, why it is needed, how it was tested, and any new edge cases or trade-offs.
