namespace ListSample

open System
open System.Windows
open System.Windows.Controls

//レコードの定義
type Data = {
    Id: string
    Title: string
}

//アプリケーションクラスの定義
type ListSampleApp() as this =
    inherit Application()

    //初期化処理
    do
        let xamlUri = new Uri("MainPage.xaml", UriKind.Relative)
        
        //Startup イベント時の処理を追加
        this.Startup.AddHandler(fun _ _ -> 
            let control = new UserControl()
            Application.LoadComponent(control, xamlUri)

            //コントロールの表示
            this.RootVisual <- control

            //ListBox 取得
            let listBox = control.FindName("listBox1") :?> ListBox

            //ListBox への表示用データ設定
            listBox.ItemsSource <- [
                {Id = "A001"; Title = "XAMLファイルを出力する方法"}
                {Id = "A002"; Title = "WPF レイアウト"}
                {Id = "B001"; Title = "F# ラムダ式の記法"}
            ]
        )

