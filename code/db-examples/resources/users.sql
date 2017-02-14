--START:add-user
-- :name add-user! :! :n
-- :doc  adds a new user
INSERT INTO users
(id, pass)
VALUES (:id, :pass)
--END:add-user

--START:add-user-returning
-- :name add-user-returning! :i :1
-- :doc  adds a new user returning the id
INSERT INTO users
(id, pass)
VALUES (:id, :pass)
returning id
--END:add-user-returning

--START:add-users
-- :name add-users! :! :n
-- :doc add multiple users
INSERT INTO users
(id, pass)
VALUES :t*:users
--END:add-users

--START:find-user
-- :name find-user :? :1
-- find the user with a matching ID
SELECT *
FROM users
WHERE id = :id
--END:find-user

--START:find-users
-- :name find-users :? :*
-- find users with a matching ID
SELECT *
FROM users
WHERE id IN (:v*:ids)
--END:find-users