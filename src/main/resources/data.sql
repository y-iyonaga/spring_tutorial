-- 課題
insert into issues (summary, description) values ('最大概要ああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああ',
    '最大詳細ああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああ最大詳細ああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああ最大詳細ああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああ最大詳細ああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああ最大詳細ああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああ最大詳細ああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああ最大詳細ああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああ最大詳細ああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああ最大詳細ああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああ最大詳細ああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああ');
insert into issues (summary, description) values ('バグA', 'バグがあります');
insert into issues (summary, description) values ('機能要望B', 'Bに追加機能がほしいです');
insert into issues (summary, description) values ('画面Cが遅い', '早くしてほしいです');
insert into issues (summary, description) values ('機能要望D', 'Dに追加機能がほしいです');
insert into issues (summary, description) values ('データベースエラー', 'DB接続時にエラーが発生する');
insert into issues (summary, description) values ('ログイン不具合', '特定の条件でログインできない');
insert into issues (summary, description) values ('UIの調整', 'デザインが崩れるので修正したい');
insert into issues (summary, description) values ('検索機能改善', '検索結果の表示速度を向上させたい');
insert into issues (summary, description) values ('メール通知バグ', 'メールが送信されないことがある');
insert into issues (summary, description) values ('APIレスポンス遅延', 'APIのレスポンスが遅いので改善したい');
insert into issues (summary, description) values ('セッション切れ', 'セッションが予期せず切れる');
insert into issues (summary, description) values ('キャッシュ問題', '古いデータがキャッシュに残る');
insert into issues (summary, description) values ('新機能リクエスト', '新しいレポート機能を追加したい');
insert into issues (summary, description) values ('データ重複バグ', '同じデータが二重登録されることがある');
insert into issues (summary, description) values ('レイアウト崩れ', '特定のブラウザでレイアウトが崩れる');
insert into issues (summary, description) values ('ログ管理', 'エラーログを詳細に記録できるようにしたい');
insert into issues (summary, description) values ('ファイルアップロード', 'ファイルアップロード機能を追加');
insert into issues (summary, description) values ('モバイル対応', 'スマホ向けの最適化を進めたい');
insert into issues (summary, description) values ('認証強化', 'パスワードポリシーを厳しくしたい');

-- 課題作成者
INSERT INTO issues_creator (issues_ID, creatorName) VALUES
(1, '田中'),
(2, '佐藤'),
(3, '鈴木'),
(4, '山田'),
(5, '高橋'),
(6, '伊藤'),
(7, '渡辺'),
(8, '中村'),
(9, '小林'),
(10, '加藤'),
(11, '吉田'),
(12, '山本'),
(13, '松本'),
(14, '井上'),
(15, '森'),
(16, '石井'),
(17, '橋本'),
(18, '岡田'),
(19, '杉山'),
(20, '藤田');
