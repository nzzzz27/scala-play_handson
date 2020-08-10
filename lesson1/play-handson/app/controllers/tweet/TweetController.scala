package controllers.tweet

//既存パッケージをimport 
  import javax.inject.{Inject, Singleton}
  //ほとんどのControllerが依存している関数
  import play.api.mvc.ControllerComponents
  
  //アクションと結果を返す
  import play.api.mvc.BaseController
  
  //httpリクエスト
  import play.api.mvc.Request
  
  //リクエストコンテンツタイプに応じたリクエストボディを生成
  import play.api.mvc.AnyContent
  
  //Formを使うため
  import play.api.data._
  import play.api.data.Forms._

import models.Tweet

case class TweetFormData(content: String)

@Singleton
class TweetController @Inject()(val controllerComponents: ControllerComponents) extends BaseController with play.api.i18n.I18nSupport {
  
  //tweetのメインコンテンツを作成
  val tweets = scala.collection.mutable.ArrayBuffer((1L to 10L).map(i => Tweet(Some(i), s"test tweet${i.toString}")): _*) 

  //tweet一覧画面
  def list() =  Action { 
    implicit request: Request[AnyContent] =>
    
    //viewの引数としてtweetsを渡す
    Ok(views.html.tweet.list(tweets.toSeq))
  }
    
  //詳細画面
  def show(id: Long) = Action { 
    implicit request: Request[AnyContent] => 
    
      tweets.find(_.id.exists(_ == id)) match {
        case Some(tweet) => Ok(views.html.tweet.show(tweet))
        case None        => NotFound(views.html.error.page404())
      }
  }

  //登録機能
  val form: Form[TweetFormData] = Form(
    mapping(
      "content" -> nonEmptyText(maxLength = 140)
    )(TweetFormData.apply)(TweetFormData.unapply)
  )
  
  def register() = Action {
    implicit request: Request[AnyContent] => 
      Ok(views.html.tweet.store(form))
  }
  
  //画面表示用 - コンパイルエラー回避用に、何もしない登録用のメゾット
  def store() = Action { implicit request: Request[AnyContent] =>
    
    form.bindFromRequest().fold(
      //処理失敗時に呼び出される関数
      (formWithErrors: Form[TweetFormData]) => {
        BadRequest(views.html.tweet.store(formWithErrors))
      },
      
      //処理が成功した場合
      (tweetFormData: TweetFormData) => {
        tweets += Tweet(Some(tweets.size + 1L), tweetFormData.content)

        //登録完了したらリダイレクト
        Redirect("/tweet/list")
      }
    )
  }

  // 編集画面を開く
  def edit(id: Long) = Action { implicit request: Request[AnyContent] =>
    tweets.find(_.id.exists(_ == id)) match {
      case Some(tweet) =>
        Ok(views.html.tweet.edit(
          id, // データを識別するためのidを渡す
          form.fill(TweetFormData(tweet.content)) // fillでformに値を詰める
        ))
      case None        =>
        NotFound(views.html.error.page404())
    }
  }

  //対象のツイートを更新する
  def update(id: Long) = Action { implicit request: Request[AnyContent] =>
    form.bindFromRequest().fold(
      (formWithErrors: Form[TweetFormData]) => {
        BadRequest(views.html.tweet.edit(id, formWithErrors))
      },
      (data: TweetFormData) => {
        tweets.find(_.id.exists(_ == id)) match {
          case Some(tweet) =>
            tweets.update(id.toInt - 1, tweet.copy(content = data.content))
            Redirect(routes.TweetController.list())
          case None        =>
            NotFound(views.html.error.page404())
        }
      }
    )
  }
  
  //対象のデータを削除する
  def delete() = Action { implicit request: Request[AnyContent] => 
    val idOpt = request.body.asFormUrlEncoded.get("id").headOption

    tweets.find(_.id.map(_.toString) == idOpt) match {
      case Some(tweet) => 
        tweets -= tweet 
        Redirect(routes.TweetController.list())
      case None        => 
        NotFound(views.html.error.page404())
    }
  }

}
