namespace ListSample

open System
open System.Windows
open System.Windows.Controls

type ListSampleApp() as this =
    inherit Application()

    do
        let xamlUri = new Uri("MainPage.xaml", UriKind.Relative)
        this.Startup.AddHandler(fun _ _ -> 
            let control = new UserControl()
            Application.LoadComponent(control, xamlUri)

            this.RootVisual <- control
        )

