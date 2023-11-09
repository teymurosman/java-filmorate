# java-filmorate

***Filmorate - проект для любителей кино***

## ER-диаграмма базы данных
![](filmorate_ER_diagram.png)

### Примеры запросов

#### Получение всех пользователей:
``` roomsql
SELECT *
FROM users;
```

#### Получение всех фильмов:
``` roomsql
SELECT f.film_id,
       f.name,
       f.description,
       f.release_date,
       f.duration,
       mpa.name,
       g.name
FROM films AS f
JOIN mpa_rating AS mpa ON f.mpa_rating_id = mpa.id
JOIN film_genre AS fg ON f.film_id = fg.film_id
WHERE f.film_id = ?;
```

#### Получение ТОП-? фильмов по количеству лайков:
``` roomsql
SELECT f.name,
       COUNT(l.film_id) AS likes_amount
FROM films AS f
JOIN likes AS l ON f.film_id = l.film_id
GROUP BY f.name
ORDER BY likes_amount DESC
LIMIT ?;
```