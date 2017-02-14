-- :name create-user! :! :n
-- creates a new user record
INSERT INTO users
(id, pass)
VALUES (:id, :pass)

--START:update-user
-- :name update-user! :! :n
-- update an existing user record
UPDATE users
SET email = :email
WHERE id = :id
--END:update-user

-- :name get-user :? :1
-- retrieve a user given the id
SELECT * FROM users
WHERE id = :id

-- :name delete-user! :! :n
-- delete a user given the id
DELETE FROM users
WHERE id = :id
