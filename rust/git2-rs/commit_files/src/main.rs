use git2::Repository;
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
    }

    Ok(())
}
