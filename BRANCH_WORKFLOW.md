# Branch Workflow

## Branch Structure

- **`prerelease`** - Development branch for testing
  - All new features and bug fixes go here first
  - Automatic builds create prerelease APKs
  - Tagged with `-prerelease` suffix

- **`main`** - Production branch (protected)
  - Only tested code from `prerelease` gets merged here
  - Automatic builds create production releases
  - No direct commits allowed (PR required)

## Workflow

1. **Development**
   ```bash
   git checkout prerelease
   # Make changes
   git add .
   git commit -m "feat: your feature"
   git push origin prerelease
   ```

2. **Testing**
   - Download prerelease APK from GitHub releases
   - Test thoroughly on device
   - Verify all features work correctly

3. **Merge to Production**
   ```bash
   # Create PR from prerelease to main
   gh pr create --base main --head prerelease --title "Release: v1.0.0"
   
   # After PR review, merge
   gh pr merge --squash
   ```

4. **Production Release**
   - Merged PR triggers production build
   - Release APK published without prerelease tag
   - Ready for distribution

## Branch Protection (Recommended)

Set up in GitHub Settings → Branches → Branch protection rules:

### For `main`:
- ✅ Require a pull request before merging
- ✅ Require approvals (optional)
- ✅ Require status checks to pass before merging
- ✅ Require branches to be up to date before merging

### For `prerelease`:
- No restrictions (allow direct pushes)
- Status checks can be enabled for safety

## Release Naming

- **Prerelease:** `v1.0.0-build123-prerelease` (marked as prerelease)
- **Production:** `v1.0.0-build124` (latest release)

## Current Status

- ✅ `prerelease` branch created
- ✅ Workflow updated to support both branches
- ✅ Prerelease builds tagged appropriately
- ⏳ Set up branch protection rules in GitHub settings
