###Скромные автотесты для https://petstore.swagger.io, с использованием JUnit 5, RestAssured

Тесты основаны на том, как указано по контракту в Swagger. Соответственно: 

- Обязательные поля только у сущности Pet: name, photoUrls

- Авторизация необходима только для вызовов:
  - PUT /user/{username}
  - DELETE /user/{username}
  - POST /user

- В методе DELETE /pet/{petId} в передаваемых параметрах указан ключ авторизации, однако он необязателен, значит авторизация для этого вызова не нужна.

- Если в вызове указано знаком "*" обязательные поля, они и считаются обязательными, и без остальных запрос должен работать.

- Предполагается, что в Swagger добавлены не все коды ошибок.

- Предполагается, что методы не работают должным образом и не всегда выдают необходие ошибки

