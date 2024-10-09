use git2::{DiffFindOptions, Repository};
use std::env;

fn main() -> Result<(), git2::Error> {
    let path = env::args().skip(1).next().unwrap_or(".".into());

    let repo = Repository::open(path)?;
    let mut walk = repo.revwalk()?;

    walk.push_head()?;

    for oid in walk {
        let commit = repo.find_commit(oid?)?;

        println!("commit id={}", commit.id());

        let tree = commit.tree()?;

        println!("  tree entries:");

        for entry in tree.iter() {
            println!(
                "    id={}, kind={:?}, name={}",
                entry.id(),
                entry.kind(),
                entry.name().unwrap_or("")
            );
        }

        let old_tree = if commit.parent_count() == 1 {
            Some(commit.parent(0)?.tree()?)
        } else {
            None
        };

        let diff = repo.diff_tree_to_tree(old_tree.as_ref(), Some(&tree), None)?;

        println!("  diff:");

        for d in diff.deltas() {
            println!(
                "    status={:?}, oldfile={}, newfile={}, nfiles={}",
                d.status(),
                d.old_file().path().and_then(|p| p.to_str()).unwrap_or(""),
                d.new_file().path().and_then(|p| p.to_str()).unwrap_or(""),
                d.nfiles()
            );
        }

        let mut diff2 = repo.diff_tree_to_tree(old_tree.as_ref(), Some(&tree), None)?;

        println!("  diff (detect renamed):");

        let mut opts = DiffFindOptions::new();
        opts.renames(true);

        diff2.find_similar(Some(&mut opts))?;

        for d in diff2.deltas() {
            println!(
                "    status={:?}, oldfile={}, newfile={}, nfiles={}",
                d.status(),
                d.old_file().path().and_then(|p| p.to_str()).unwrap_or(""),
                d.new_file().path().and_then(|p| p.to_str()).unwrap_or(""),
                d.nfiles()
            );
        }
    }

    Ok(())
}
