<img src="http://img.jcabi.com/logo.png" width="200px" height="48px" />

More details are here: [github.jcabi.com](http://github.jcabi.com/)

Set of classes in `com.jcabi.github` package
is an object oriented API of Github:

```java
public class Main {
  public static void main(String[] args) {
    Github github = new DefaultGithub(".. your OAuth token ..");
    Repo repo = github.repo("jcabi/jcabi-github");
    Issue issue = repo.issues().create("How are you?", "Please tell me...");
    issue.post("My first comment!");
  }
}
```

You need just this dependency:

```xml
<dependency>
  <groupId>com.jcabi</groupId>
  <artifactId>jcabi-github</artifactId>
  <version>0.6.1</version>
</dependency>
```

## Questions?

If you have any questions about the framework, or something doesn't work as expected,
please [submit an issue here](https://github.com/jcabi/jcabi-github/issues/new).
If you want to discuss, please use our [Google Group](https://groups.google.com/forum/#!forum/jcabi).

## How to contribute?

Fork the repository, make changes, submit a pull request.
We promise to review your changes same day and apply to
the `master` branch, if they look correct.

Please run Maven build before submitting a pull request:

```
$ mvn clean install -Pqulice
```
