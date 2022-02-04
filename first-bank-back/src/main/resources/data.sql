insert into cards(id, card_holder_name, pan, security_code, expiration_date, account_id) values (0, 'Pera Detlic', 'x4Z59ZSxPLjSv1KvSAUPjYhOyd7xYLF2iHSwmmsUa78=', '9KW16MS81+azSi3rs7OiNw==', '2028-03-11', 0);
insert into cards(id, card_holder_name, pan, security_code, expiration_date, account_id) values (1, 'Pera Detlic', '97wCVPmliyk/QbOcfno0vohOyd7xYLF2iHSwmmsUa78=', '9KW16MS81+azSi3rs7OiNw==', '2028-11-11', 0);

insert into accounts(id, balance, currency, m_id, m_password) values (0, 48965, 0, 603759, '3jgcvKq9WaZyinn3oeqWPw/y8GwByeS+UZtddLu/qzI=');
insert into accounts_cards(account_id, cards_id) values (0, 0);
insert into accounts_cards(account_id, cards_id) values (0, 1);

insert into cards(id, card_holder_name, pan, security_code, expiration_date, account_id) values (2, 'Emina Turkovic', 'Dhs3bllAOLp033IejTTAU4hOyd7xYLF2iHSwmmsUa78=', '9KW16MS81+azSi3rs7OiNw==', '2028-11-11', 1);
insert into accounts(id, balance, currency, m_id, m_password) values (1, 500000, 1, 1, 'eCwlUHW62NGrxBV58PW/5A==');
insert into accounts_cards(account_id, cards_id) values (1, 2);

insert into exchange_rate(id, src, dest, rate) values (1, 1, 0, 120);
insert into exchange_rate(id, src, dest, rate) values (2, 2, 0, 100);
insert into exchange_rate(id, src, dest, rate) values (3, 1, 2, 1.2);