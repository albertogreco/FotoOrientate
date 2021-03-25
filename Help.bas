B4A=true
Group=Default Group
ModulesStructureVersion=1
Type=Activity
Version=10.6
@EndOfDesignText@
#Region  Activity Attributes 
	#FullScreen: False
	#IncludeTitle: True
#End Region

Sub Process_Globals
	'These global variables will be declared once when the application starts.
	'These variables can be accessed from all modules.

End Sub

Sub Globals
	'These global variables will be redeclared each time the activity is created.
	'These variables can only be accessed from this module.

	Private Button1 As Button

	Private lblPath As Label
	Private Label6 As Label
End Sub


Sub Activity_Create(FirstTime As Boolean)
	'Do not forget to load the layout file created with the visual designer. For example:
	'Activity.LoadLayout("Layout1")
	Activity.LoadLayout("Layout1")
	lblPath.Text=Main.MyFolder
	Label6.Text="I campi sono separati da virgole"& CRLF &"1) nome file con data e ora"& CRLF &"2) numero foto nella sessione"& CRLF &"3) yaw"& CRLF &"4) roll"& CRLF &"5) pitch"
End Sub

Sub Activity_Resume

End Sub

Sub Activity_Pause (UserClosed As Boolean)

End Sub


Private Sub Button1_Click
	StartActivity("Main")
	Activity.Finish
End Sub