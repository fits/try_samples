use git2::{Delta, DiffFile, DiffFindOptions, Repository};
use std::collections::HashMap;
use std::env;

fn main() -> Result<(), git2::Error> {
    let path = env::args().skip(1).next().unwrap_or(".".into());

    let repo = Repository::open(path)?;
    let mut walk = repo.revwalk()?;

    walk.push_head()?;

    let mut store = HashMap::<String, u32>::new();

    for oid in walk {
        let commit = repo.find_commit(oid?)?;
        let tree = commit.tree()?;

        let old_tree = if commit.parent_count() == 1 {
            Some(commit.parent(0)?.tree()?)
        } else {
            None
        };

        let mut diff = repo.diff_tree_to_tree(old_tree.as_ref(), Some(&tree), None)?;

        let mut opts = DiffFindOptions::new();
        opts.renames(true);

        diff.find_similar(Some(&mut opts))?;

        for d in diff.deltas() {
            match d.status() {
                Delta::Added | Delta::Deleted | Delta::Modified => {
                    if let Some(f) = path_to_str(&d.new_file()) {
                        *store.entry(f.into()).or_insert(0) += 1;
                    }
                }
                Delta::Renamed => {
                    println!(
                        "# renamed: {} => {}",
                        path_to_str(&d.old_file()).unwrap_or(""),
                        path_to_str(&d.new_file()).unwrap_or(""),
                    );
                }
                _ => {}
            }
        }
    }

    for (k, v) in store {
        println!("{},{}", v, k);
    }

    Ok(())
}

fn path_to_str<'a>(f: &'a DiffFile) -> Option<&'a str> {
    f.path().and_then(|x| x.to_str())
}
