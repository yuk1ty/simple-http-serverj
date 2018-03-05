# simple-http-serverj
Java で書かれた簡易的な HTTP サーバーです．ローカルホストなどで起動し，`curl` で `1234` ポートでなおかつ `/hello` に対して `GET` を投げると，「Hello, NioHttpServer!」という文字列が返ってきます．

## 構成
このサンプルには，次のサーバーが入っています．

* `java.io` を用いたブロッキングの HTTP サーバー．
* `java.nio` を用いたノンブロッキングの HTTP サーバー．

両方とも，TCP を受け取り，リクエストを一旦バイト列に直して，内部で文字列に変換します．その後，リクエストのうちどのアドレスにアクセスしたかをパースして判別し，リクエストをハンドリングします．

文字列のパースは `RequestHandler` クラスで行っています．また，受け取ったレスポンスのハンドリングは `ResponseHandler` クラスで行っています．

## 起動

### ブロッキング HTTP サーバーを起動する

```java
public static void main(String[] args) {
    try {
      HttpServer httpServer =
          HttpServerBuilder.of()
              .port(1234)
              .addHandler(new ResponseHandler())
              .addHandler(new RequestHandler())
              .build();
      EXECUTOR_SERVICE.execute(WorkerThreadFactory.of().newThread(httpServer));
    } catch (Exception e) {
      LOGGER.error(e.getCause().getMessage());
    } finally {
      EXECUTOR_SERVICE.shutdown();
    }
}
```

### ノンブロッキング HTTP サーバーを起動する

```java
public static void main(String[] args) {
    try {
      NioHttpServer httpServer =
          NioHttpServerBuilder.of()
              .inetSocketAddress(new InetSocketAddress(1234))
              .addHandler(new ResponseHandler())
              .addHandler(new RequestHandler())
              .build();
      EXECUTOR_SERVICE.execute(WorkerThreadFactory.of().newThread(httpServer));
    } catch (Exception e) {
      LOGGER.error(e.getCause().getMessage());
    } finally {
      EXECUTOR_SERVICE.shutdown();
    }
}
```