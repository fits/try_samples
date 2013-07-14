# encoding: utf-8
# language: ja
機能: ログイン

  シナリオ: ログイン成功
    前提 Topへアクセス
    もし "username" へ "admin" を入力
    かつ "password" へ "admin" を入力
    かつ "ログイン" ボタンをクリックする
    ならば ログイン済みとなる

  シナリオ: ログイン失敗
    前提 Topへアクセス
    もし "username" へ "invaliduser" を入力
    かつ "password" へ "aaa" を入力
    かつ "ログイン" ボタンをクリックする
    ならば エラー表示

