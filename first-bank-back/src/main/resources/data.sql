insert into cards(id, card_holder_name, pan, security_code, expiration_date, account_id) values (0, 'Pera Detlic', 'k34LyLPsvwJss1cWhdL3pH8Jz+iqMtJWW0ur6k1ahUc=', 'ON9LD0M7Skm6uJqLazHKuA==', '2028-03-11', 0);
insert into cards(id, card_holder_name, pan, security_code, expiration_date, account_id) values (1, 'Pera Detlic', '3gZX50aTHobYUuEJLtbUsH8Jz+iqMtJWW0ur6k1ahUc=', 'ON9LD0M7Skm6uJqLazHKuA==', '2028-11-11', 0);

insert into accounts(id, balance, currency, m_id, m_password) values (0, 48965, 0, 603759, 'aB8fWdyEl7HhGGgZXxcyOI2Woq6atn5y9oGk+/ph2TQ=');
insert into accounts_cards(account_id, cards_id) values (0, 0);
insert into accounts_cards(account_id, cards_id) values (0, 1);

insert into cards(id, card_holder_name, pan, security_code, expiration_date, account_id) values (2, 'Emina Turkovic', 'PsZMJsa01JQOJkM4YveXuH8Jz+iqMtJWW0ur6k1ahUc=', 'ON9LD0M7Skm6uJqLazHKuA==', '2028-11-11', 1);
insert into accounts(id, balance, currency, m_id, m_password) values (1, 500000, 1, 1, 'wgm/CwWb3IioIStMBaSor42Woq6atn5y9oGk+/ph2TQ=');
insert into accounts_cards(account_id, cards_id) values (1, 2);

insert into exchange_rate(id, src, dest, rate) values (1, 1, 0, 120);
insert into exchange_rate(id, src, dest, rate) values (2, 2, 0, 100);
insert into exchange_rate(id, src, dest, rate) values (3, 1, 2, 1.2);