<img src="http://img.jcabi.com/logo-square.png" width="64px" height="64px" />

[![EO principles respected here](http://www.elegantobjects.org/badge.svg)](http://www.elegantobjects.org)
[![Managed by Zerocracy](https://www.0crat.com/badge/C3RUBL5H9.svg)](https://www.0crat.com/p/C3RUBL5H9)
[![DevOps By Rultor.com](http://www.rultor.com/b/jcabi/jcabi-github)](http://www.rultor.com/p/jcabi/jcabi-github)
[![We recommend IntelliJ IDEA](http://www.elegantobjects.org/intellij-idea.svg)](https://www.jetbrains.com/idea/)

[![Build Status](https://travis-ci.org/jcabi/jcabi-github.svg?branch=master)](https://travis-ci.org/jcabi/jcabi-github)
[![PDD status](http://www.0pdd.com/svg?name=jcabi/jcabi-github)](http://www.0pdd.com/p?name=jcabi/jcabi-github)
[![Build status](https://ci.appveyor.com/api/projects/status/rdhq60kvt75ic3cv/branch/master?svg=true)](https://ci.appveyor.com/project/yegor256/jcabi-github/branch/master)
[![JavaDoc](https://img.shields.io/badge/javadoc-html-blue.svg)](http://www.javadoc.io/doc/com.jcabi/jcabi-github)

[![jpeek report](http://i.jpeek.org/com.jcabi/jcabi-github/badge.svg)](http://i.jpeek.org/com.jcabi/jcabi-github/)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.jcabi/jcabi-github/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.jcabi/jcabi-github)
[![Dependencies](https://www.versioneye.com/user/projects/561a9e43a193340f2800106e/badge.svg?style=flat)](https://www.versioneye.com/user/projects/561a9e43a193340f2800106e)

More details are here: [github.jcabi.com](http://github.jcabi.com/). Also,
read this blog post: [Object-Oriented Github API](http://www.yegor256.com/2014/05/14/object-oriented-github-java-sdk.html).
Java 7 or higher is required.

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
a new Github OAuth access tokens
([how?](https://help.github.com/articles/creating-an-access-token-for-command-line-use)),
and provide them in command line, like this:

```
$ mvn clean install -Dit.test=RtGistITCase -Dfailsafe.github.key=<token> -Dfailsafe.github.key.second=<second-token> -Dfailsafe.github.repo=<repo>
```

Replace `<token>` and `<second-token>` with the OAuth access tokens of two different Github
accounts. This test case will try to fork a gist from first account into second. Replace
`<repo>` with the name of repository you create in your first account (for test purposes
only), for example `yegor256/test`. OAuth access tokens should have permissions in their
respective repos to all scopes needed by the integration test suite you want to run
(including `delete_repo`, which is not set by default!).

Please note that different integration tests may need keys with permissions to different
[scopes](https://developer.github.com/v3/oauth/#scopes).
To run all integration tests, the key should have the following OAuth scopes:
- read:org
- repo
- delete_repo
- admin:public_key
- gist
- admin:repo_hook
- user
- user:email

`RtForksITCase` requires additional parameter `-Dfailsafe.github.organization=<organization>`
where `<organization>` is an organization name to fork test github repository.

In order to run static analysis checks only use this:

```
$ mvn clean install -DskipTests -Dinvoker.skip=true -Pqulice
```

