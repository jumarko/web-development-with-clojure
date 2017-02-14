-- :name create-user! :! :n
-- creates a new user record
INSERT INTO users
(id, pass)
VALUES (:id, :pass)

-- :name update-user! :! :n
-- update an existing user record
UPDATE users
SET email = :email
WHERE id = :id

-- :name get-user :? :1
-- retrieve a user given the id
SELECT * FROM users
WHERE id = :id

-- :name delete-user! :! :n
-- delete a user given the id
DELETE FROM users
WHERE id = :id

--START:save-file
-- :name save-file! :! :n
-- saves a file to the database
INSERT INTO files
(owner, type, name, data)
VALUES (:owner, :type, :name, :data)
--END:save-file
