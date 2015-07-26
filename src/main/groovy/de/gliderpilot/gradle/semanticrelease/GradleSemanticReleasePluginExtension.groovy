package de.gliderpilot.gradle.semanticrelease

import org.ajoberstar.gradle.git.release.semver.PartialSemVerStrategy
import org.ajoberstar.gradle.git.release.semver.SemVerStrategy
import org.ajoberstar.gradle.git.release.semver.StrategyUtil
import org.ajoberstar.grgit.Commit
import org.ajoberstar.grgit.Grgit
import org.gradle.util.ConfigureUtil

/**
 * Created by tobias on 7/21/15.
 */
class GradleSemanticReleasePluginExtension {

    final Grgit grgit = Grgit.open()
    final GradleSemanticReleaseCommitMessageConventions commitMessageConventions = new GradleSemanticReleaseCommitMessageConventions()
    final PartialSemVerStrategy semanticStrategy = new GradleSemanticReleaseStrategy(grgit, commitMessageConventions)
    final PartialSemVerStrategy onReleaseBranch = new GradleSemanticReleaseCheckReleaseBranchStrategy()
    final PartialSemVerStrategy appendBranchName = new GradleSemanticReleaseAppendBranchNameStrategy()

    def commitMessages(Closure closure) {
        ConfigureUtil.configure(closure, commitMessageConventions)
    }

    def releaseBranches(Closure closure) {
        ConfigureUtil.configure(closure, onReleaseBranch)
    }

    def appendBranchNames(Closure closure) {
        ConfigureUtil.configure(closure, appendBranchName)
    }

    SemVerStrategy toSemanticReleaseStrategy(SemVerStrategy strategy) {
        strategy.copyWith(
                normalStrategy: semanticStrategy,
                preReleaseStrategy: StrategyUtil.all(appendBranchName, strategy.preReleaseStrategy),
                buildMetadataStrategy: StrategyUtil.all(strategy.buildMetadataStrategy, onReleaseBranch)
        )
    }

}