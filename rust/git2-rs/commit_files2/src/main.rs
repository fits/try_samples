use git2::{Delta, DiffFile, DiffOptions, DiffFindOptions, Repository};
use std::env;

type Result<T> = std::result::Result<T, git2::Error>;

fn main() -> Result<()> {
    let path = env::args().skip(1).next().unwrap_or(".".into());

    let repo = Repository::open(path)?;
    let mut walk = repo.revwalk()?;

    walk.push_head()?;

    for oid in walk {
        let commit = repo.find_commit(oid?)?;
        let tree = commit.tree()?;

        let old_tree = if commit.parent_count() == 1 {
            Some(commit.parent(0)?.tree()?)
        } else {
            None
        };

        let mut opts = DiffOptions::new();
        opts.minimal(true); 

        let mut diff = repo.diff_tree_to_tree(old_tree.as_ref(), Some(&tree), Some(&mut opts))?;

        let mut fopts = DiffFindOptions::new();
        fopts.renames(true);

        diff.find_similar(Some(&mut fopts))?;

        for d in diff.deltas() {
            match d.status() {
                Delta::Added | Delta::Deleted | Delta::Modified => {
                    if let Some(f) = path_to_str(&d.new_file()) {
                        println!("{}", f);
                    }
                }
                Delta::Renamed => {
                    println!(
                        "{}, {}",
                        path_to_str(&d.old_file()).unwrap_or(""),
                        path_to_str(&d.new_file()).unwrap_or(""),
                    );
                }
                _ => {}
            }
        }
    }

    Ok(())
}

fn path_to_str<'a>(f: &'a DiffFile) -> Option<&'a str> {
    f.path().and_then(|x| x.to_str())
}
