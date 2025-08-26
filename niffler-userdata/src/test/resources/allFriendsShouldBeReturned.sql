INSERT INTO public."user" (id, username, currency, firstname, surname, photo, photo_small, full_name)
VALUES ('a9165b45-a4aa-47d6-ac50-43611d624421', 'Lisa', 'USD', null, null, null, null, null);

INSERT INTO public."user" (id, username, currency, firstname, surname, photo, photo_small, full_name)
VALUES ('a9165b45-a4aa-47d6-ac50-43611d624422', 'LisaFriend', 'EUR', null, null, null, null, null);

INSERT INTO public.friendship (requester_id, addressee_id, status, created_date)
VALUES ('a9165b45-a4aa-47d6-ac50-43611d624421', 'a9165b45-a4aa-47d6-ac50-43611d624422', 'ACCEPTED', '2025-08-14');

INSERT INTO public.friendship (requester_id, addressee_id, status, created_date)
VALUES ('a9165b45-a4aa-47d6-ac50-43611d624422', 'a9165b45-a4aa-47d6-ac50-43611d624421', 'ACCEPTED', '2025-08-14');
