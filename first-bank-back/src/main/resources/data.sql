insert into cards(id, card_holder_name, pan, security_code, expiration_date, account_id) values (0, 'Pera Detlic', '0000000000000000', '334', '2028-03-11', 0);
insert into cards(id, card_holder_name, pan, security_code, expiration_date, account_id) values (1, 'Pera Detlic', '0000000000000220', '334', '2028-11-11', 0);
insert into accounts(id, balance, currency, m_id, m_password) values (0, 48965, 0, 603759, 'lNut7PxikZScvkKdoipq');
insert into accounts_cards(account_id, cards_id) values (0, 0);
insert into accounts_cards(account_id, cards_id) values (0, 1);

insert into cards(id, card_holder_name, pan, security_code, expiration_date, account_id) values (2, 'Emina Turkovic', '0000000000000001', '334', '2028-11-11', 1);
insert into accounts(id, balance, currency, m_id, m_password) values (1, 500000, 1, 1, '111111');
insert into accounts_cards(account_id, cards_id) values (1, 2);

insert into exchange_rate(id, src, dest, rate) values (1, 1, 0, 120);
insert into exchange_rate(id, src, dest, rate) values (2, 2, 0, 100);
insert into exchange_rate(id, src, dest, rate) values (3, 1, 2, 1.2);