<img src="http://img.jcabi.com/logo-square.png" width="64px" height="64px" />

[![Made By Teamed.io](http://img.teamed.io/btn.svg)](http://www.teamed.io)
[![DevOps By Rultor.com](http://www.rultor.com/b/jcabi/jcabi-github)](http://www.rultor.com/p/jcabi/jcabi-github)

[![Build Status](https://travis-ci.org/jcabi/jcabi-github.svg?branch=master)](https://travis-ci.org/jcabi/jcabi-github)
[![Build status](https://ci.appveyor.com/api/projects/status/rdhq60kvt75ic3cv/branch/master?svg=true)](https://ci.appveyor.com/project/yegor256/jcabi-github/branch/master)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.jcabi/jcabi-github/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.jcabi/jcabi-github)

More details are here: [github.jcabi.com](http://github.jcabi.com/). Also,
read this blog post: [Object-Oriented Github API](http://www.yegor256.com/2014/05/14/object-oriented-github-java-sdk.html).

Set of classes in `com.jcabi.github` package is
an object oriented API of Github:

```java
public class Main {
  public static void main(String[] args) throws IOException {
    Github github = new RtGithub(".. your OAuth token ..");
    Repo repo = github.repos().get(
        new Coordinates.Simple("jcabi/jcabi-github")
    );
    Issue issue = repo.issues().create("How are you?", "Please tell me...");
    issue.comments().post("My first comment!");
  }
}
```

We also provide `MkGithub`, a mock version of Github server, which
you can use in unit tests, for example:

```java
public class FooTest {
  public void submitsCommentToGithubIssue() {
    final Repo repo = new MkGithub().repos().create(
      Json.createObjectBuilder().add("name", "test").build()
    );
    final Issue issue = repo.issues().create("how are you?", "");
    new Foo(issue).doSomething(); // should post a message to the issue
    MasterAssert.assertThat(
      issue.comments().iterate(),
      Matchers.iterableWithSize(1)
    );
  }
}
```

## Questions?

If you have any questions about the framework, or something doesn't work as expected,
please [submit an issue here](https://github.com/jcabi/jcabi-github/issues/new).

## How to contribute?

Fork the repository, make changes, submit a pull request.
We promise to review your changes same day and apply to
the `master` branch, if they look correct.

Please run Maven (3.1 or higher!) build before submitting a pull request:

```
$ mvn clean install -Pqulice
```

There are many integration tests that check our classes against
live Github accounts. In order to run them, you should create
a new Github OAuth access token
([how?](https://help.github.com/articles/creating-an-access-token-for-command-line-use)),
and provide it in command line, like this:

```
$ mvn clean install -Dit.test=RtGistITCase -Dfailsafe.github.key=<token> -Dfailsafe.github.repo=<repo>
```

Replace `<token>` with the OAuth access token, and `<repo>` with the name of
repository you create in your account (for test purposes only), for example `yegor256/test`.
The `failsafe.github.key` should have permissions in `failsafe.github.repo` to all scopes needed
by the integration test suite you want to run (including `delete_repo`, which is not set by default!).
Please note that different integration tests may need keys with permissions to different
[scopes](https://developer.github.com/v3/oauth/#scopes);
the `RtGistITCase` test requires permissions to gist scope.
`RtForksITCase` requires additional parameter `-Dfailsafe.github.organization=<organization>`
where `<organization>` is an organization name to fork test github repository.

In order to run static analysis checks only use this:

```
$ mvn clean install -DskipTests -Dinvoker.skip=true -Pqulice
```
