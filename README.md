# Сервис выбора фильмов 

Это серверная программа Java, которая поможет выбрать фильм для просмотра. Пользователи могут добавлять друзей, ставить лайки фильмам и получать рекомендации.
## Функции

1. Управление пользователями: создание и редактирование учетных записей пользователей.
2. Управление фильмами: добавление и редактирование фильмов.
3. Дружба: пользователи могут добавлять других пользователей в друзья.
4. Понравившиеся фильмы: пользователи могут ставить лайки фильмам.
5. API-взаимодействие: программа предоставляет API для интеграции с другими приложениями.

## API-взаимодействие

Примеры запросов API:

1. **Создание пользователя**
    - Эндпоинт: `POST /users`
    - Тело запроса:
      ```
      {
        "login": "dolore",
        "name": "Nick Name",
        "email": "mail@mail.ru",
        "birthday": "2000-08-20"
      }
      ```
    - Ответ:
      ```
      {
        "id": 1,
        "email": "mail@mail.ru",
        "login": "dolore",
        "name": "Nick Name",
        "birthday": "2000-08-20",
        "friends": []
      }
      ```

2. **Добавление фильма**
    - Эндпоинт: `POST /films`
    - Тело запроса:
      ```
      {
        "name": "nisi eiusmod",
        "description": "adipisicing",
        "releaseDate": "1967-03-25",
        "duration": 100
      }
      ```
    - Ответ:
      ```
      {
        "id": 1,
        "name": "nisi eiusmod",
        "description": "adipisicing",
        "releaseDate": "1967-03-25",
        "duration": 100,
        "likes": []
      }
      ```

3. **Добавление в друзья**
    - Эндпоинт: `PUT /users/{id}/friends/{friendId}`

4. **Поставить лайк**
    - Эндпоинт: `PUT /films/{id}/like/{userId}`

## ER-диаграмма

![ER-диаграмма](https://github.com/blooocckk/java-filmorate/blob/main/src/main/java/ru/yandex/practicum/filmorate/ER.png?raw=true)

ER-диаграмма иллюстрирует сущности и их связи в программе.

1. **users**: Представляет объект пользователя. Атрибуты:
   - `id`: уникальный идентификатор каждого пользователя
   - `email`: адрес электронной почты
   - `login`: логин
   - `name`: имя
   - `birthday`: дата рождения

2. **films**: Представляет объект фильма. Атрибуты:
   - `id`: уникальный идентификатор каждого фильма
   - `name`: название
   - `description`: описание
   - `releaseDate`: год выхода
   - `duration`: длительность
   - `rating_id`: идентификатор соответствующего рейтинга

3. **rating**: Представляет объект рейтинга. Атрибуты:
   - `id`: уникальный идентификатор каждого рейтинга
   - `name`: название
   
4. **film_genre**: Представляет соответствие фильма и жанров. Атрибуты:
   - `film_id`: идентификатор фильма
   - `genre_id`: идентификатор соответствующего жанра

5. **genre**: Представляет объект жанра. Атрибуты:
   - `genre_id`: уникальный идентификатор каждого жанра
   - `name`: название
   
6. **friendship**: Представляет дружеские связи между пользователями. Атрибуты
   - `user_id`: идентификатор пользователя
   - `friend_id`: идентификатор друга
   - `status`: статус дружбы
   
7. **likes**: Представляет связь пользователей и понравившихся им фильмов. Атрибуты:
   - `user_id`: идентификатор пользователя
   - `film_id`: идентификатор фильма

Примеры запросов SQL:
1. **Получить все фильмы**
     ```
       SELECT * 
       FROM films;
     ```
2. **Получить всех пользователей**
     ```
       SELECT * 
       FROM users;
     ```
3. **Получить фильм по ID**
     ```
       SELECT * 
       FROM films 
       WHERE id = <film_id>;
     ```
4. **Получить пользователя по ID**
     ```
       SELECT * 
       FROM users 
       WHERE id = <user_id>;
     ```
5. **Получить фильмы, выпущенные после определенной даты**
     ```
       SELECT * 
       FROM films 
       WHERE releaseDate > '<date>';
     ```
6. **Получить имена пользователей, поставивших лайк определенному фильму**
     ```
       SELECT users.name
       FROM users
       JOIN likes ON users.id = likes.user_id
       WHERE likes.film_id = 5;
     ```
7. **Получить названия пяти самых популярных фильмов и количество лайков**
     ```
       SELECT f.name AS film_name, 
              COUNT(l.film_id) AS like_count
       FROM films AS f
       JOIN likes AS l ON f.id = l.film_id
       GROUP BY f.id
       ORDER BY like_count DESC
       LIMIT 5;
     ```
8. **Получить ID и имена общих друзей**
     ```
       SELECT u2.id, 
              u2.name
       FROM friendship AS f1
       JOIN friendship AS f2 ON f1.friend_id = f2.friend_id AND f1.user_id = <user1_id> AND f2.user_id = <user2_id>
       JOIN users AS u2 ON f1.friend_id = u2.id
       WHERE f1.status = true AND f2.status = true;
     ```