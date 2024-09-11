INSERT INTO tb_user (email, password) VALUES ('david@gmail.com', '$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG');
INSERT INTO tb_user (email, password) VALUES ('gustavo@gmail.com', '$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG');

INSERT INTO tb_role (authority) VALUES ('ROLE_CLIENT');
INSERT INTO tb_role (authority) VALUES ('ROLE_ADMIN');

INSERT INTO tb_user_role (user_id,role_id) VALUES (1,1);
INSERT INTO tb_user_role (user_id,role_id) VALUES (2,2);

INSERT INTO tb_product (uuid,name,description,price,type,product_pictue_url,stock,user_id) VALUES ('97e630e3-637a-4930-b4e9-8fcf48fda319','teste','oi',200.0,'ELETRONICS','https://myawsbucketgustavolyra.s3.us-east-2.amazonaws.com/4d8ff0ea-cc7a-4d5a-b5b1-baab6114e6e1_b9f6556d41672ce8d07752900a656cfd.jpg',999,2);
-- INSERT INTO tb_basket (id) VALUES (1);
-- INSERT INTO tb_basket_item (product_id,quantity,basket_id) VALUES ('97e630e3-637a-4930-b4e9-8fcf48fda319',1,1);