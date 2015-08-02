/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.gliderpilot.gradle.semanticrelease

import org.ajoberstar.grgit.Commit
import spock.lang.Specification
import spock.lang.Subject

/**
 * Created by tobias on 7/26/15.
 */
class GradleSemanticReleaseCommitMessageConventionsSpec extends Specification {

    @Subject
    GradleSemanticReleaseCommitMessageConventions conventions = new GradleSemanticReleaseCommitMessageConventions()

    def "finds references tickets one on each line"() {
        given:
        Commit commit = new Commit(fullMessage: '''\
            Closes #123
            Fixes #456
        '''.stripIndent())

        expect:
        conventions.closes(commit) == ['123', '456'] as SortedSet
    }

    def "finds references tickets all on one line"() {
        given:
        Commit commit = new Commit(fullMessage: '''\
            Closes #123, #456
        '''.stripIndent())

        expect:
        conventions.closes(commit) == ['123', '456'] as SortedSet
    }

    def "finds breaking change on same line"() {
        given:
        Commit commit = new Commit(fullMessage: '''\
            Subject

            BREAKING CHANGE: foo bar baz
        '''.stripIndent())

        expect:
        conventions.breaks(commit) == 'foo bar baz'
    }

    def "finds breaking change on next lines"() {
        given:
        Commit commit = new Commit(fullMessage: '''\
            Subject

            BREAKING CHANGE:

            foo bar baz
        '''.stripIndent())

        expect:
        conventions.breaks(commit) == 'foo bar baz'
    }

    def "finds type from shortMessage"() {
        given:
        Commit commit = new Commit(shortMessage: shortMessage)

        expect:
        conventions.type(commit) == type

        where:
        shortMessage | type
        "did this and that" | null
        "feat(core): blah blupp" | "feat"
        "feat: blah blupp" | "feat"
    }

    def "finds component from shortMessage"() {
        given:
        Commit commit = new Commit(shortMessage: shortMessage)

        expect:
        conventions.component(commit) == component

        where:
        shortMessage | component
        "did this and that" | null
        "feat(core): blah blupp" | "core"
        "feat: blah blupp" | null
    }

    def "subject does not contain type and component"() {
        given:
        Commit commit = new Commit(shortMessage: shortMessage)

        expect:
        conventions.subject(commit) == subject

        where:
        shortMessage | subject
        "did this and that" | "did this and that"
        "feat(core): blah blupp" | "blah blupp"
        "feat: blah blupp" | "blah blupp"
    }

}
