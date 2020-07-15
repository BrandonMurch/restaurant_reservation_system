insert into users(id, email, first_name, last_name, phone_number,
terms_and_conditions)
values(10001, 'thisisanemail@email.com', 'John', 'Doe', '+61 304940380808',
1);
insert into users(id, email, first_name, last_name, phone_number,
terms_and_conditions)
values(10002, 'another@email.com', 'Alfred', 'Johnson', '+61 76887986587',
1);
insert into users(id, email, first_name, last_name, phone_number,
terms_and_conditions)
values(10003, 'yetanother@email.com', 'Nina', 'Lastname', '+61 5858588695',
1);

insert into booking(id, end_time, party_size, start_time, user_id)
values(10000, '20201211 20:00:00.00',  4, '20201211 17:00:00.00', 10001);
insert into booking(id, end_time, party_size, start_time, user_id)
values(10001, '20201211 23:00:00.00',  4, '20201211 20:00:00.00', 10002);
insert into booking(id, end_time, party_size, start_time, user_id)
values(10003, '20201210 23:00:00.00',  2, '20201210 20:00:00.00', 10001);